package edu.gatech.sad.project4.engine;

import edu.gatech.sad.project4.NewHibernateUtil;
import edu.gatech.sad.project4.entities.Coursetable;
import edu.gatech.sad.project4.entities.Offeredcoursestable;
import edu.gatech.sad.project4.entities.Processingstatustable;
import edu.gatech.sad.project4.entities.Professorcourseassignmenttable;
import edu.gatech.sad.project4.entities.ProfessorcourseassignmenttableId;
import edu.gatech.sad.project4.entities.Professorstable;
import edu.gatech.sad.project4.entities.Studentcourseassignmenttable;
import edu.gatech.sad.project4.entities.StudentcourseassignmenttableId;
import edu.gatech.sad.project4.entities.Studentpreferencestable;
import edu.gatech.sad.project4.entities.Studenttable;
import edu.gatech.sad.project4.entities.Tacourseassignmenttable;
import edu.gatech.sad.project4.entities.TacourseassignmenttableId;
import edu.gatech.sad.project4.hometables.OfferedcoursestableHome;
import edu.gatech.sad.project4.hometables.ProcessingstatustableHome;
import edu.gatech.sad.project4.hometables.ProfessorcourseassignmenttableHome;
import edu.gatech.sad.project4.hometables.StudentcourseassignmenttableHome;
import edu.gatech.sad.project4.hometables.StudentpreferencestableHome;
import edu.gatech.sad.project4.hometables.TacourseassignmenttableHome;
import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class CoreComputeEngine {
	private static final Log log = LogFactory.getLog(CoreComputeEngine.class);

	public final static int DEFAULT_STUDENT_MAX_COURSES = 2;
	public final static int DEFAULT_PROFESSOR_MAX_COURSES = 1;
	public final static int DEFAULT_TA_MAX_COURSES = 1;

	private CoreComputeEngineDataHolder ccedh = null;
	private int processingStatusId;

	// Gurobi objects
	private GRBModel model;
	private GRBVar[][] studentCourseVars;
	private GRBVar[][] professorCourseVars;
	private GRBVar[][] taCourseVars;

	/**
	 * Constructs a CoreComputeEngine instance for the supplied ProcessingId.
	 * 
	 * @param processingStatusId
	 */
	public CoreComputeEngine(int processingStatusId) {
		log.debug("Constructing CoreComputeEngine for processingStatusId:" + processingStatusId);
		this.processingStatusId = processingStatusId;

		// update PS record with execution start time
		Session s = NewHibernateUtil.getSessionFactory().getCurrentSession();
		Transaction t = s.beginTransaction();
		ProcessingstatustableHome psh = new ProcessingstatustableHome();
		Processingstatustable ps = psh.findById(processingStatusId);
		if (ps == null) {
			log.error("did not find requested processingStatusId:" + processingStatusId);
			System.exit(1);
		}
		ps.setExecutionStartTime(new Date());
		t.commit();
	}

	/**
	 * Run Gurobi to produce a constraint based solution.
	 */
	public void solve() {
		ccedh = new CoreComputeEngineDataHolder();

		Studenttable[] students = ccedh.getStudents();
		Coursetable[] courses = ccedh.getCourses();
		Professorstable[] professors = ccedh.getProfessors();
		Studenttable[] taPool = ccedh.getTaPool();

		// The Gurobi environment
        GRBEnv env = null;
		try {
			env = new GRBEnv();

			// instantiate a model
			model = new GRBModel(env);
			
			// setup studentCourse assignment vars
			studentCourseVars = new GRBVar[students.length][courses.length];
			for (int i = 0; i < students.length; i++) {
				Studenttable student = students[i];
				for (int j = 0; j < courses.length; j++) {
					Coursetable course = courses[j];
					String varName = "Student[" + student.getId() + "]_Course[" + course.getCourseCode() + "]";
					studentCourseVars[i][j] = model.addVar(0.0, 1, 0.0, GRB.BINARY, varName);
				}
			}

			// setup professorCourse assignment vars
			professorCourseVars = new GRBVar[professors.length][courses.length];
			for (int i = 0; i < professors.length; i++) {
				Professorstable professor = professors[i];
				for (int j = 0; j < courses.length; j++) {
					Coursetable course = courses[j];
					String varName = "Professor[" + professor.getProfessorId() + "]_Course[" + course.getCourseCode() + "]";
					professorCourseVars[i][j] = model.addVar(0.0, 1, 0.0, GRB.BINARY, varName);
				}
			}

			// setup taCourse assignment vars
			taCourseVars = new GRBVar[taPool.length][courses.length];
			for (int i = 0; i < taPool.length; i++) {
				Studenttable ta = taPool[i];
				for (int j = 0; j < courses.length; j++) {
					Coursetable course = courses[j];
					String varName = "TA[" + ta.getId() + "]_Course[" + course.getCourseCode() + "]";
					taCourseVars[i][j] = model.addVar(0.0, 1, 0.0, GRB.BINARY, varName);
				}
			}

			// Integrate new variables
            model.update();

            // define objective
            // we want to maximize the total number of students taking courses.
			GRBLinExpr objective = new GRBLinExpr();
			for (int i = 0; i < studentCourseVars.length; i++) {
				for (int j = 0; j < studentCourseVars[i].length; j++) {
					objective.addTerm(1, studentCourseVars[i][j]);
				}
			}
            model.setObjective(objective, GRB.MAXIMIZE);

            // CONSTRAINTS
            applyConstraints();

            // Optimize the model
            model.optimize();

            // retrieve the result of the optimization
            double objectiveValue = model.get(GRB.DoubleAttr.ObjVal);

            // write results to the DB
            generateResults();
		} catch (GRBException e) {
			e.printStackTrace();
		} finally {
			if (env != null) {
				try {
					env.dispose();
				} catch(Exception e) {}
			}
		}
	}

	/**
	 * Convenience method to put all the constraint calls in one place.
	 * 
	 * @throws GRBException
	 */
	private void applyConstraints() throws GRBException {
		// student takes courses from "desired" list
		constrainDesiredCourses();
		// student takes up to "Studentpreferencestable.numCoursesDesired" or system-wide maxCourses
		constrainMaxCoursesPerStudent();
		// student must take prereq first
		constrainPrereqs();
		// student priority based on how many courses taken (from Studenttable.takenCourses)
		constrainStudentSeniority();
		// course must have a professor assigned
		constrainCourseByProfessorAssigned();
		// professor must have competency in that course
		constrainCourseByProfessorCompetency();
		// professor can teach 1 course (???)
		constrainMaxCoursesPerProfessor();
		// ta must have taken course
		constrainTaMustHaveTakenCourse();
		// each ta increases course capacity by 50
		// max students in course derived from numberOfTas
		constrainMaxStudentPerCourse();
	}

	/**
	 * Constrain courses to only those desired by the student.  We do this
	 * by constraining the sum of all "not desired" courses to 0.
	 */
	private void constrainDesiredCourses() throws GRBException {
		// student takes courses from "desired" list
		Studenttable[] students = ccedh.getStudents();
		Coursetable[] courses = ccedh.getCourses();
		Map<Integer, List<String>> studentCoursesWantedMap = ccedh.getStudentCoursesWantedMap();
		for (int i = 0; i < studentCourseVars.length; i++) {
			Studenttable student = students[i];
			List<String> coursesWantedList = studentCoursesWantedMap.get(student.getId());
			GRBLinExpr desiredCourseConstraint = new GRBLinExpr();
			for (int j = 0; j < studentCourseVars[i].length; j++) {
				Coursetable course = courses[j];
				if (!coursesWantedList.contains(course.getCourseCode())) {
					desiredCourseConstraint.addTerm(1, studentCourseVars[i][j]);
				}
			}
			String constraintName = "DESIREDCOURSESONLY_Student[" + i + "]";
	       	model.addConstr(desiredCourseConstraint, GRB.EQUAL, 0, constraintName);
		}
	}

	/**
	 * Constrain total number of classes a student takes.  This is the lower of
	 * the system wide max course value (hardcoded to 2 in this version) and the
	 * desiredNumCourses from the preferences.
	 */
	private void constrainMaxCoursesPerStudent() throws GRBException {
		// student takes up to "Studentpreferencestable.numCoursesDesired" or system-wide maxCourses
		Studenttable[] students = ccedh.getStudents();
		Map<Integer, Studentpreferencestable> studentStudentPrefsMap = ccedh.getStudentStudentPrefsMap();
		for (int i = 0; i < studentCourseVars.length; i++) {
			Studenttable student = students[i];
			Studentpreferencestable studentPrefs = studentStudentPrefsMap.get(student.getId());
			int numCoursesDesired = studentPrefs.getNumCoursesDesired();
			if (numCoursesDesired == 0) {
				// student doesn't want any courses
				continue;
			}
			// numCoursesDesired can't exceed system-wide maxCourses setting
			if (numCoursesDesired > DEFAULT_STUDENT_MAX_COURSES) {
				numCoursesDesired = DEFAULT_STUDENT_MAX_COURSES;
			}

			GRBLinExpr maxCoursePerStudentConstraint = new GRBLinExpr();
			for (int j = 0; j < studentCourseVars[i].length; j++) {
				maxCoursePerStudentConstraint.addTerm(1, studentCourseVars[i][j]);
			}
			String constraintName = "MAXCOURSE_Student[" + i + "]";
	       	model.addConstr(maxCoursePerStudentConstraint, GRB.EQUAL, numCoursesDesired, constraintName);
		}
	}

	private void constrainPrereqs() {
		// student must take prereq first
	}

	private void constrainStudentSeniority() {
		// student priority based on how many courses taken (from Studenttable.takenCourses)
	}

	private void constrainCourseByProfessorAssigned() {
		// course must have a professor assigned
	}

	/**
	 * Constrain the courses that a professor can teach.
	 * We do this by creating a constraint that will force all
	 * classes a teach doesn't have a competency in to sum to zero.
	 */
	private void constrainCourseByProfessorCompetency() throws GRBException {
		// professor can teach 1 course (???)
		Professorstable professors[] = ccedh.getProfessors();
		Coursetable courses[] = ccedh.getCourses();
		for (int i = 0; i < professorCourseVars.length; i++) {
			Professorstable professor = professors[i];
			Map<Integer, Set<String>> professorCompetenciesMap = ccedh.getProfessorCompetenciesMap();
			Set<String> competencies = professorCompetenciesMap.get(professor.getProfessorId());
	        GRBLinExpr courseByProfessorcompetencyConstraint = new GRBLinExpr();
			for (int j = 0; j < professorCourseVars[i].length; j++) {
				Coursetable course = courses[j];
				if (!competencies.contains(course.getCourseCode())) {
					// only add terms for courses the professor *doesn't* teach
					courseByProfessorcompetencyConstraint.addTerm(1, professorCourseVars[i][j]);
				};
			}
			String constraintName = "COMPETENCIESONLY_Professor[" + i + "]";
	       	model.addConstr(courseByProfessorcompetencyConstraint, GRB.EQUAL, 0, constraintName);
		}
	}

	/**
	 * Contrain the number of classes the professor can teach to a max of 1.
	 * We only consider the courses where the professor has an assigned competency.
	 */
	private void constrainMaxCoursesPerProfessor() throws GRBException {
		// professor can teach 1 course (???)
		Professorstable professors[] = ccedh.getProfessors();
		Coursetable courses[] = ccedh.getCourses();
		for (int i = 0; i < professorCourseVars.length; i++) {
			Professorstable professor = professors[i];
			Map<Integer, Set<String>> professorCompetenciesMap = ccedh.getProfessorCompetenciesMap();
			Set<String> competencies = professorCompetenciesMap.get(professor.getProfessorId());
	        GRBLinExpr maxCoursePerProfessorConstraint = new GRBLinExpr();
			for (int j = 0; j < professorCourseVars[i].length; j++) {
				Coursetable course = courses[j];
				if (competencies.contains(course.getCourseCode())) {
					maxCoursePerProfessorConstraint.addTerm(1, professorCourseVars[i][j]);
				};
			}
			String constraintName = "MAXPROFCOURSE_Professor[i]";
	       	model.addConstr(maxCoursePerProfessorConstraint, GRB.LESS_EQUAL, 1, constraintName);
		}
	}

	private void constrainTaMustHaveTakenCourse() {
		// ta must have taken course
	}

	private void constrainMaxStudentPerCourse() {
		// each ta increases course capacity by 50
		// max students in course derived from numberOfTas
	}

	// write results to DB
	private void generateResults() {
		
		Studenttable[] students = ccedh.getStudents();
		Map<Integer, Studentpreferencestable> studentStudentPrefsMap = ccedh.getStudentStudentPrefsMap();
		Coursetable[] courses = ccedh.getCourses();
		Professorstable[] professors = ccedh.getProfessors();
		Studenttable[] taPool = ccedh.getTaPool();

		Set<String> offeredCourseSet = new HashSet<String>();

		StudentcourseassignmenttableHome scah = new StudentcourseassignmenttableHome();
		ProfessorcourseassignmenttableHome pcah = new ProfessorcourseassignmenttableHome();
		OfferedcoursestableHome och = new OfferedcoursestableHome();
		TacourseassignmenttableHome tch = new TacourseassignmenttableHome();
		StudentpreferencestableHome sph = new StudentpreferencestableHome();

		Session s = NewHibernateUtil.getSessionFactory().getCurrentSession();
		Transaction t = s.beginTransaction();
		try {
			// read out studentCourse assignment vars
			for (int i = 0; i < students.length; i++) {
				StringBuilder sb = new StringBuilder();
				for (int j = 0; j < courses.length; j++) {
					double x = studentCourseVars[i][j].get(GRB.DoubleAttr.X);
					if (x > 0) {
						if (sb.length() > 0) {
							sb.append(",");
						}
						sb.append(courses[j].getCourseCode());
						offeredCourseSet.add(courses[j].getCourseCode());
					}
				}
				if (sb.length() > 0) {
					StudentcourseassignmenttableId scai = new StudentcourseassignmenttableId();
					scai.setProcessingStatusId(processingStatusId);
					scai.setStudentId(students[i].getId());
					Studentcourseassignmenttable sca = new Studentcourseassignmenttable();
					sca.setId(scai);
					sca.setCourseCode(sb.toString());
					scah.persist(sca);

					// update Studentprefs with processingId
					Studentpreferencestable sp = studentStudentPrefsMap.get(students[i].getId());
					sp.setProcessingStatusId(processingStatusId);
				}
			}

			// read out effective course offerings
			if (offeredCourseSet.size() > 0) {
				StringBuilder sb = new StringBuilder();
				for (String courseCode : offeredCourseSet) {
					if (sb.length() > 0) {
						sb.append(",");
					}
					sb.append(courseCode);
				}
				Offeredcoursestable oc = new Offeredcoursestable();
				oc.setProcessingStatusId(processingStatusId);
				oc.setCourseCode(sb.toString());
				och.persist(oc);
			}

			// read out professorCourse assignment vars
			for (int i = 0; i < professors.length; i++) {
				StringBuilder sb = new StringBuilder();
				for (int j = 0; j < courses.length; j++) {
					double x = professorCourseVars[i][j].get(GRB.DoubleAttr.X);
					if (x > 0) {
						if (sb.length() > 0) {
							sb.append(",");
						}
						sb.append(courses[j].getCourseCode());
					}
				}
				if (sb.length() > 0) {
					ProfessorcourseassignmenttableId pcai = new ProfessorcourseassignmenttableId();
					pcai.setProcessingStatusId(processingStatusId);
					pcai.setProfessorId(professors[i].getProfessorId());
					Professorcourseassignmenttable pca = new Professorcourseassignmenttable();
					pca.setId(pcai);
					pca.setCourseCode(sb.toString());
					pcah.persist(pca);
				}
			}
			
			// read out taCourse assignment vars
			for (int i = 0; i < taPool.length; i++) {
				StringBuilder sb = new StringBuilder();
				for (int j = 0; j < courses.length; j++) {
					double x = taCourseVars[i][j].get(GRB.DoubleAttr.X);
					if (x > 0) {
						if (sb.length() > 0) {
							sb.append(",");
						}
						sb.append(courses[j].getCourseCode());
					}
				}
				TacourseassignmenttableId tci = new TacourseassignmenttableId();
				tci.setProcessingStatusId(processingStatusId);
				tci.setStudentId(taPool[i].getId());
				Tacourseassignmenttable tac = new Tacourseassignmenttable();
				tac.setId(tci);
				tac.setCourseCode(sb.toString());
				tch.persist(tac);
			}

			// update processingstatus to indicate complete
			ProcessingstatustableHome psh = new ProcessingstatustableHome();
			Processingstatustable ps = psh.findById(processingStatusId);
			ps.setCompleted(true);

			t.commit();
		} catch (GRBException e) {
			e.printStackTrace();
			if (t != null && t.isActive()) {
				t.rollback();
			}
		}
	}

}

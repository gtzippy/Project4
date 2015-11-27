package edu.gatech.sad.project4.engine;

import edu.gatech.sad.project4.NewHibernateUtil;
import edu.gatech.sad.project4.entities.Coursetable;
import edu.gatech.sad.project4.entities.Offeredcoursestable;
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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class CoreComputeEngine {
	private CoreComputeEngineDataHolder ccedh = new CoreComputeEngineDataHolder();
	private GRBModel model;
	private GRBVar[][] studentCourseVars;
	private GRBVar[][] professorCourseVars;
	private GRBVar[][] taCourseVars;
	private int processingStatusId;

	public CoreComputeEngine(int processingStatusId) {
		this.processingStatusId = processingStatusId;
		solve();
		generateResults();
	}

	// run the gurobi solver
	private void solve() {
		Studenttable[] students = ccedh.getStudents();
		Coursetable[] courses = ccedh.getCourses();
		Professorstable[] professors = ccedh.getProfessors();
		Studenttable[] taPool = ccedh.getTaPool();
		// The Gurobi environment
        GRBEnv env;
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

            // retrieve the results
            double objectiveValue = model.get(GRB.DoubleAttr.ObjVal);
		} catch (GRBException e) {
			e.printStackTrace();
		}
	}

	private void applyConstraints() {
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

	private void constrainDesiredCourses() {
		// student takes courses from "desired" list
	}

	private void constrainMaxCoursesPerStudent() {
		// student takes up to "Studentpreferencestable.numCoursesDesired" or system-wide maxCourses
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

	private void constrainCourseByProfessorCompetency() {
		// professor must have competency in that course
	}

	private void constrainMaxCoursesPerProfessor() {
		// professor can teach 1 course (???)
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
		Map<Integer, Integer> studentStudentPrefsMap = ccedh.getStudentStudentPrefsMap();
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
					int spId = studentStudentPrefsMap.get(students[i].getId());
					Studentpreferencestable sp = sph.findById(spId);
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
			
			t.commit();
		} catch (GRBException e) {
			e.printStackTrace();
			if (t != null && t.isActive()) {
				t.rollback();
			}
		}
	}

}

package edu.gatech.sad.project4.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.gatech.sad.project4.NewHibernateUtil;
import edu.gatech.sad.project4.entities.Coursetable;
import edu.gatech.sad.project4.entities.Professorstable;
import edu.gatech.sad.project4.entities.Studentpreferencestable;
import edu.gatech.sad.project4.entities.Studenttable;

public class CoreComputeEngineDataHolder {
	private static final Log log = LogFactory.getLog(CoreComputeEngineStub.class);

	// Course related data
	private Coursetable[] courses = new Coursetable[0];
	private Map<String, Coursetable> courseMap = new HashMap<String, Coursetable>();

	// Student related data
	private Studenttable[] students = new Studenttable[0];
	private Map<Integer, List<String>> studentCoursesWantedMap = new HashMap<Integer, List<String>>();
	private Map<Integer, Set<String>> studentCoursesTakenMap = new HashMap<Integer, Set<String>>();
	private Map<Integer, Studentpreferencestable> studentStudentPrefsMap = new HashMap<Integer, Studentpreferencestable>();

	// Professor related data
	private Professorstable[] professors = new Professorstable[0];
	private Map<Integer, Set<String>> professorCompetenciesMap = new HashMap<Integer, Set<String>>();

	// TA related data
	private Set<Integer> taPoolSet = new HashSet<Integer>();
	private Studenttable[] taPool = new Studenttable[0];

	/**
	 * loads all data for a run of the Core compute Engine
	 */
	public CoreComputeEngineDataHolder() {
		buildCourseData();
		buildStudentData();
		buildProfessorData();
		buildTaPoolData();
	}

	/**
	 * Loads all offered courses (courses the school wants to teach this semester).
	 */
	private void buildCourseData() {
		// load all courses
		Session s = NewHibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = s.beginTransaction();
		List<Coursetable> courseList = s.createQuery("SELECT c FROM Coursetable c where c.offered = TRUE").list();
		courses = courseList.toArray(courses);

		// build map for lookup
		for (Coursetable course : courses) {
			courseMap.put(course.getCourseCode(), course);
		}
		transaction.commit();
		
	}

	/**
	 * Loads all active students (students that want to take a course this semester).
	 * Also loads their most current preferences selections.
	 */
	private void buildStudentData() {
		// load all students
		Session s = NewHibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = s.beginTransaction();
		List<Studenttable> studentList = s.createCriteria(Studenttable.class).list();
		students = studentList.toArray(students);

		for (Studenttable student : students) {
			// build coursesTakenMap
			Set<String> coursesTaken = new HashSet<String>();
			String coursesTakenString = student.getTakenCourses();
			if (StringUtils.isNotEmpty(coursesTakenString)) {
				StringTokenizer st = new StringTokenizer(coursesTakenString, ",");
				while (st.hasMoreTokens()) {
					String courseCode = st.nextToken();
					coursesTaken.add(courseCode);
				}
			}
			studentCoursesTakenMap.put(student.getId(), coursesTaken);

			// for each student, load most recent studentPreferences
			Query q = s.createQuery("SELECT sp FROM Studentpreferencestable sp where sp.studentId = :studentId ORDER BY sp.date");
			q.setParameter("studentId", student.getId());
			List<Studentpreferencestable> spl = q.list();
			if (spl.size() == 0) {
				// no preferences
				continue;
			}
			Studentpreferencestable sp = spl.get(0);
			if (sp.getNumCoursesDesired() == 0) {
				// skip if student doesn't want to take any classes next semester
				continue;
			}
			String coursesString = sp.getCourses();
			if (StringUtils.isEmpty(coursesString)) {
				// skip if student doesn't want to take any classes next semester
				continue;
			}

			// save the sp id so we can update the correct record later
			studentStudentPrefsMap.put(student.getId(), sp);
			
			// build coursesWantedList
			List<String> coursesWantedList = new ArrayList<String>();
			String coursesWantedString = sp.getCourses();
			if (StringUtils.isNotEmpty(coursesWantedString)) {
				StringTokenizer st = new StringTokenizer(coursesWantedString, ",");
				while (st.hasMoreTokens()) {
					String courseCode = st.nextToken();
					// don't bother with classes that aren't available
					if (courseMap.containsKey(courseCode)) {
						coursesWantedList.add(courseCode);
					}
				}
			}
			studentCoursesWantedMap.put(student.getId(), coursesWantedList);
		}
		transaction.commit();
	}

	/**
	 * Loads all professors.
	 */
	private void buildProfessorData() {
		// load all professors
		Session s = NewHibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = s.beginTransaction();
		List<Professorstable> professorList = s.createCriteria(Professorstable.class).list();
		professors = professorList.toArray(professors);

		// load courses this professor can teach
		for (Professorstable professor : professors) {
			Set<String> competencies = new HashSet<String>();
			String coursesString = professor.getCourses();
			if (StringUtils.isNotEmpty(coursesString)) {
				StringTokenizer st = new StringTokenizer(coursesString, ",");
				while (st.hasMoreTokens()) {
					String coursecode = st.nextToken();
					if (courseMap.containsKey(coursecode)) {
						competencies.add(coursecode);
					}
				}
			}
			professorCompetenciesMap.put(professor.getProfessorId(), competencies);
		}
		transaction.commit();
	}

	/**
	 * Loads all TA students.
	 */
	private void buildTaPoolData() {
		List<Studenttable> studentTableList = new ArrayList<Studenttable>();
		for (Studenttable student : students) {
			if (student.isTa()) {
				taPoolSet.add(student.getId());
				studentTableList.add(student);
			}
		}
		taPool = studentTableList.toArray(taPool);
	}

	/**
	 * 
	 * @return
	 */
	public Coursetable[] getCourses() {
		return courses;
	}

	/**
	 * 
	 * @return
	 */
	public Studenttable[] getStudents() {
		return students;
	}

	/**
	 * 
	 * @return
	 */
	public Professorstable[] getProfessors() {
		return professors;
	}

	/**
	 * 
	 * @return
	 */
	public Studenttable[] getTaPool() {
		return taPool;
	}

	/**
	 * 
	 * @return
	 */
	public Map<Integer, Studentpreferencestable> getStudentStudentPrefsMap() {
		return studentStudentPrefsMap;
	}

	/**
	 * 
	 * @return
	 */
	public Map<Integer, List<String>> getStudentCoursesWantedMap() {
		return studentCoursesWantedMap;
	}

	/**
	 * 
	 * @return
	 */
	public Map<Integer, Set<String>> getStudentCoursesTakenMap() {
		return studentCoursesTakenMap;
	}

	/**
	 * 
	 * @return
	 */
	public Map<Integer, Set<String>> getProfessorCompetenciesMap() {
		return professorCompetenciesMap;
	}

}

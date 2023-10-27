package gui;

import dao.AssignmentDaoInterface;
import dao.CourseDaoInterface;
import dao.impl.AssignmentDao;
import domain.User;
import dao.impl.CourseDao;
import util.FrameUtil;

import javax.swing.*;
import java.util.List;
import java.util.Objects;

public class SelectAssignmentGUI extends JFrame{
    private JPanel panel;
    private JButton backButton;
    private JButton checkButton;
    private JList<String> courseList;
    private JList<String> assignmentList;
    private JScrollPane coursePane;
    private JScrollPane assignmentPane;
    private JPanel pnlList;
    private JLabel courses;
    private JLabel assignments;
    private JButton createNewButton;
    private JButton deleteButton;
    private JLabel explainLabel;
    private JButton correctButton;

    public SelectAssignmentGUI(User user){
        // In this GUI, you can select an assignment from selected courses to submit or arrange.
        if(user.isAdmin()){
            explainLabel.setText("Correct, Alter, New or Delete assignments.");
        } else {
            explainLabel.setText("Select an assignment, completing and submitting it. Let's go!");
            correctButton.setText("Select");
            checkButton.setVisible(false);
            createNewButton.setVisible(false);
            deleteButton.setVisible(false);
        }

        AssignmentDaoInterface assignmentDao = new AssignmentDao();
        CourseDaoInterface courseDao = new CourseDao();

        DefaultListModel<String> courseListModel = new DefaultListModel<>();
        DefaultListModel<String> assignmentListModel = new DefaultListModel<>();

        // Get the courseList and convert it to courseNameList showed in courseList
        List<String> CourseNames = courseDao.getCourseNames(courseDao.getCourseByUser(user));

        for (String assignmentName : CourseNames) {
            courseListModel.addElement(assignmentName);
        }

        // Set the model for the lists
        courseList.setModel(courseListModel);
        assignmentList.setModel(assignmentListModel);

        // According to the selected course, show the assignments of that
        courseList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                // Retrieve the selected course
                String selectedCourse = courseList.getSelectedValue();

                // Convert the selectedCourse to assignmentNames.
                List<String> assignmentNames = assignmentDao.getAssignmentNamesByCourseID(courseDao.getCourseIDByName(selectedCourse));

                // Clear the assignmentListModel
                assignmentListModel.clear();

                for (String assignmentName : assignmentNames) {
                    assignmentListModel.addElement(assignmentName);
                }

                assignmentList.setModel(assignmentListModel);
            }
        });

        backButton.addActionListener(e -> {
            SelectAssignmentGUI.this.dispose();
            new MainGUI(user);
        });

        deleteButton.addActionListener(e -> {
            String selectedCourse = courseList.getSelectedValue();
            String selectedAssignment = assignmentList.getSelectedValue();

            if(Objects.equals(selectedAssignment, null)){
                FrameUtil.showConfirmation(SelectAssignmentGUI.this, "You haven't selected any assignment!");
            } else {
                if(assignmentDao.deleteAssignment(assignmentDao.getAssignmentByAssignmentAndCourseName(selectedAssignment, selectedCourse))){
                    FrameUtil.showConfirmation(SelectAssignmentGUI.this, "Delete successfully!");
                } else {
                    FrameUtil.showConfirmation(SelectAssignmentGUI.this, "Something wrong or the assignment doesn't exist");
                }
            }
            new SelectAssignmentGUI(user);
        });

        createNewButton.addActionListener(e -> {
            SelectAssignmentGUI.this.dispose();
            new ManageAssignmentGUI(user);
        });

        checkButton.addActionListener(e -> {
            String selectedCourse = courseList.getSelectedValue();
            String selectedAssignment = assignmentList.getSelectedValue();

            if(Objects.equals(selectedAssignment, null)){
                FrameUtil.showConfirmation(SelectAssignmentGUI.this, "You haven't selected any assignment!");
                new SelectAssignmentGUI(user);
            } else {
                SelectAssignmentGUI.this.dispose();
                new ManageAssignmentGUI(user, assignmentDao.getAssignmentByAssignmentAndCourseName(selectedAssignment, selectedCourse));
            }
        });

        correctButton.addActionListener(e -> {
            String selectedCourse = courseList.getSelectedValue();
            String selectedAssignment = assignmentList.getSelectedValue();

            if(Objects.equals(selectedAssignment, null)){
                FrameUtil.showConfirmation(SelectAssignmentGUI.this, "You haven't selected any assignment!");
                new SelectAssignmentGUI(user);
            } else {
                if (user.isAdmin()){
                    SelectAssignmentGUI.this.dispose();
                    new CorrectOrCheckGUI(user, assignmentDao.getAssignmentByAssignmentAndCourseName(selectedAssignment, selectedCourse));
                } else {
                    SelectAssignmentGUI.this.dispose();
                    new SubmissionGUI(user, assignmentDao.getAssignmentByAssignmentAndCourseName(selectedAssignment, selectedCourse));
                }
            }
        });

        setContentPane(panel);
        setTitle("Select your assignment");
        setSize(600, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
    }
}

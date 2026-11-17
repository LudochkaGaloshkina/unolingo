package com.valek.view.dashboard;

import com.valek.model.learningmodels.Course;
import com.valek.model.learningmodels.CourseUnit;
import com.valek.model.learningmodels.Lesson;
import com.valek.service.learningservices.CourseUnitService;
import com.valek.service.learningservices.LessonService;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CourseDetailsView {

    private final CourseUnitService courseUnitService;
    private final LessonService lessonService;

    public VBox create(Course course) {

        VBox root = new VBox(20);

        Label title = new Label(course.getTitle());
        Label description = new Label(course.getDescription());

        List<CourseUnit> units =
                courseUnitService.getUnitsByCourse(course.getId());

        root.getChildren().addAll(title, description);

        for (CourseUnit unit : units) {
            Label unitTitle = new Label(
                    unit.getPosition() + ". " + unit.getTitle()
            );

            root.getChildren().add(unitTitle);

            List<Lesson> lessons =
                    lessonService.getPublishedLessonsByUnit(unit.getId());

            for (Lesson lesson : lessons) {
                Label lessonTitle = new Label(
                        "    " + lesson.getPosition() + ". " + lesson.getTitle()
                );

                root.getChildren().add(lessonTitle);
            }
        }

        return root;
    }
}
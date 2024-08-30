package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class completedTask {
    private int taskId;
    private String taskTitle;
    private String taskDescription;
    private LocalDate doneDate;
}

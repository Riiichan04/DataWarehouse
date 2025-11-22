package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Process {
    private int id;
    private String name;
    private String description;
    private String scriptName;
    private String typeProcess;
    private String targetPath;
}
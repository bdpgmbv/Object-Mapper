package com.vy.objectmapper.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FieldSetup {
    public List<String> identityFieldPaths;
    public List<String> outputFieldPaths;
    public String ignoreFieldPath;
    public boolean ignore;
    public double numberTolerance;
}

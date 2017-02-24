package com.intellij.dhcc;

import com.intellij.openapi.project.Project;
import com.intellij.util.Processor;

/**
 * Created by Administrator on 2017/2/24.
 */
public class TextAnalyze {

    public static final String MARKABLE="@mark";
    public static final int MAARKABLE_LENGTH=MARKABLE.length();

    public static void list(String text, Project project,final Processor<Object> processor){
        String[] lines=text.split("\n");
        for(int i=0;i<lines.length;i++){
            String line=lines[i];
            int location=line.indexOf(MARKABLE);
            if(location>-1){
                processor.process(new MLocationFile(project,line.substring(location+MAARKABLE_LENGTH).trim(),i+1));
            }
        }
    }
}

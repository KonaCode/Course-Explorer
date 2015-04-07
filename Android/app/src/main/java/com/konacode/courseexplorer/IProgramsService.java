package com.konacode.courseexplorer;

import java.util.List;

/**
 * Created by Ryan on 3/24/2015.
 */
public interface IProgramsService
{
   public int count();

   public boolean clear();

   public Program create(Program program);

   public List<Program> retrieveAll();

   public Program update(Program program);

   public Program delete(Program program);
}

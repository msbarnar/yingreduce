package edu.asu.ying.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.grapher.GrapherModule;
import com.google.inject.grapher.InjectorGrapher;
import com.google.inject.grapher.graphviz.GraphvizModule;
import com.google.inject.grapher.graphviz.GraphvizRenderer;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import edu.asu.ying.wellington.WellingtonModule;

/**
 *
 */
public class DependencyGraph {

  @Test
  public void graphTest() throws IOException {
    Injector injector = Guice.createInjector(new WellingtonModule());
    String path = "dependencies.dot";
    System.out.println("Writing dependency graph to ".concat(path));
    graphDependencies(path, injector);
  }

  private void graphDependencies(String filename, Injector demoInjector) throws IOException {
    PrintWriter out = new PrintWriter(new File(filename), "UTF-8");

    Injector injector = Guice.createInjector(new GrapherModule(), new GraphvizModule());
    GraphvizRenderer renderer = injector.getInstance(GraphvizRenderer.class);
    renderer.setOut(out).setRankdir("TB");

    injector.getInstance(InjectorGrapher.class)
        .of(demoInjector)
        .graph();
  }
}

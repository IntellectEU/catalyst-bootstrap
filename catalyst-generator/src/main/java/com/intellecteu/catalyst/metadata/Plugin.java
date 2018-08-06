package com.intellecteu.catalyst.metadata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Taras Shvyryd
 */
public class Plugin extends Dependency {

  private final static int DEFAULT_PLUGIN_INDENT = 3;
  private String body;

  public Plugin(Dependency dependency){
    super(dependency);
    try {
      body = loadBody();
    } catch (IOException e) { e.printStackTrace(); }
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  private String loadBody() throws IOException {
    String path = "catalyst-generator/src/main/resources/templates/" + getFacets().get(0);
    String indent = IntStream.range(0, DEFAULT_PLUGIN_INDENT).mapToObj(i -> "\t").collect(Collectors.joining());
    String result = new String(Files.readAllBytes(Paths.get(path)));
    return result.replaceAll("\n", "\n"+indent);
  }
}

/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intellecteu.catalyst.generator;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.intellecteu.catalyst.InitializrException;
import com.intellecteu.catalyst.metadata.BillOfMaterials;
import com.intellecteu.catalyst.metadata.Dependency;
import com.intellecteu.catalyst.metadata.InitializrConfiguration.Env.Maven.ParentPom;
import com.intellecteu.catalyst.metadata.InitializrMetadata;
import com.intellecteu.catalyst.metadata.InitializrMetadataProvider;
import com.intellecteu.catalyst.util.TemplateRenderer;
import com.intellecteu.catalyst.util.Version;
import com.intellecteu.catalyst.util.VersionProperty;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.Assert;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StreamUtils;

/**
 * Generate a project based on the configured metadata.
 *
 * @author Dave Syer
 * @author Stephane Nicoll
 * @author Sebastien Deleuze
 * @author Andy Wilkinson
 */
public class ProjectGenerator {

  private static final Logger log = LoggerFactory.getLogger(ProjectGenerator.class);

  private static final Version VERSION_1_2_0_RC1 = Version.parse("1.2.0.RC1");

  private static final Version VERSION_1_3_0_M1 = Version.parse("1.3.0.M1");

  private static final Version VERSION_1_4_0_M2 = Version.parse("1.4.0.M2");

  private static final Version VERSION_1_4_0_M3 = Version.parse("1.4.0.M3");

  private static final Version VERSION_1_4_2_M1 = Version.parse("1.4.2.M1");

  private static final Version VERSION_1_5_0_M1 = Version.parse("1.5.0.M1");

  private static final Version VERSION_2_0_0_M1 = Version.parse("2.0.0.M1");

  private static final Version VERSION_2_0_0_M3 = Version.parse("2.0.0.M3");

  private static final Version VERSION_2_0_0_M6 = Version.parse("2.0.0.M6");
  private static final String GRADLE = "gradle";
  private static final String MAVEN = "maven";
  private static final String WAR = "war";
  private static final String KOTLIN = "kotlin";

  private final String LICENSE = "/*\n"
      + "* Copyright (c) 2018 IntellectEU NV. All rights reserved.\n"
      + "* Redistribution and use in source and binary forms are not permitted without direct consent of IntellectEU NV in writing.\n"
      + "* THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, \n"
      + "* EXPRESS OR IMPLIED,INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, \n"
      + "* FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. \n"
      + "* IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, \n"
      + "* DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, \n"
      + "* ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.\n"
      + "*/";

  @Autowired
  private ApplicationEventPublisher eventPublisher;

  @Autowired
  private InitializrMetadataProvider metadataProvider;

  @Autowired
  private ProjectRequestResolver requestResolver;

  @Autowired
  private TemplateRenderer templateRenderer = new TemplateRenderer();

  @Autowired
  private ProjectResourceLocator projectResourceLocator = new ProjectResourceLocator();

  @Value("${TMPDIR:.}/initializr")
  private String tmpdir;

  private File temporaryDirectory;
  private transient Map<String, List<File>> temporaryFiles = new LinkedHashMap<>();

  private static boolean isJava8OrLater(ProjectRequest request) {
    return !request.getJavaVersion().equals("1.6")
        && !request.getJavaVersion().equals("1.7");
  }

  private static boolean isGradleBuild(ProjectRequest request) {
    return GRADLE.equals(request.getBuild());
  }

  private static boolean isMavenBuild(ProjectRequest request) {
    return MAVEN.equals(request.getBuild());
  }

  private static boolean isWar(ProjectRequest request) {
    return WAR.equals(request.getPackaging());
  }

  private static boolean isNewTestInfrastructureAvailable(ProjectRequest request) {
    return VERSION_1_4_0_M2
        .compareTo(Version.safeParse(request.getBootVersion())) <= 0;
  }

  private static boolean isGradle3Available(Version bootVersion) {
    return VERSION_1_5_0_M1.compareTo(bootVersion) <= 0;
  }

  private static boolean isGradle4Available(Version bootVersion) {
    return VERSION_2_0_0_M3.compareTo(bootVersion) < 0;
  }

  private static List<Dependency> filterDependencies(List<Dependency> dependencies,
      String scope) {
    return dependencies.stream()
        .filter(dep -> scope.equals(dep.getScope()))
        .filter(dep -> dep.shouldAlwaysHaveArtifactCoordinates())
        .sorted(DependencyComparator.INSTANCE)
        .collect(Collectors.toList());
  }

  public InitializrMetadataProvider getMetadataProvider() {
    return metadataProvider;
  }

  public void setMetadataProvider(InitializrMetadataProvider metadataProvider) {
    this.metadataProvider = metadataProvider;
  }

  public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }

  public void setRequestResolver(ProjectRequestResolver requestResolver) {
    this.requestResolver = requestResolver;
  }

  public void setTemplateRenderer(TemplateRenderer templateRenderer) {
    this.templateRenderer = templateRenderer;
  }

  public void setProjectResourceLocator(ProjectResourceLocator projectResourceLocator) {
    this.projectResourceLocator = projectResourceLocator;
  }

  public void setTmpdir(String tmpdir) {
    this.tmpdir = tmpdir;
  }

  public void setTemporaryFiles(Map<String, List<File>> temporaryFiles) {
    this.temporaryFiles = temporaryFiles;
  }

  /**
   * Generate a Maven pom for the specified {@link ProjectRequest}.
   */
  public byte[] generateMavenPom(ProjectRequest request) {
    try {
      Map<String, Object> model = resolveModel(request);
      if (!isMavenBuild(request)) {
        throw new InvalidProjectRequestException("Could not generate Maven pom, "
            + "invalid project type " + request.getType());
      }
      byte[] content = doGenerateMavenPom(model);
      publishProjectGeneratedEvent(request);
      return content;
    } catch (InitializrException ex) {
      publishProjectFailedEvent(request, ex);
      throw ex;
    }
  }

  /**
   * Generate a Gradle build file for the specified {@link ProjectRequest}.
   */
  public byte[] generateGradleBuild(ProjectRequest request) {
    try {
      Map<String, Object> model = resolveModel(request);
      if (!isGradleBuild(request)) {
        throw new InvalidProjectRequestException(
            "Could not generate Gradle build, " + "invalid project type "
                + request.getType());
      }
      byte[] content = doGenerateGradleBuild(model);
      publishProjectGeneratedEvent(request);
      return content;
    } catch (InitializrException ex) {
      publishProjectFailedEvent(request, ex);
      throw ex;
    }
  }

  /**
   * Generate a project structure for the specified {@link ProjectRequest}. Returns a directory
   * containing the project.
   */
  public File generateProjectStructure(ProjectRequest request) {
    try {
      Map<String, Object> model = resolveModel(request);
      File rootDir = generateProjectStructure(request, model);
      publishProjectGeneratedEvent(request);
      return rootDir;
    } catch (InitializrException ex) {
      publishProjectFailedEvent(request, ex);
      throw ex;
    }
  }

  /**
   * Generate a project structure for the specified {@link ProjectRequest} and resolved model.
   */
  protected File generateProjectStructure(ProjectRequest request,
      Map<String, Object> model) {
    File rootDir;
    StringBuilder appProperties = new StringBuilder();
    try {
      rootDir = File.createTempFile("tmp", "", getTemporaryDirectory());
    } catch (IOException e) {
      throw new IllegalStateException("Cannot create temp dir", e);
    }
    addTempFile(rootDir.getName(), rootDir);
    rootDir.delete();
    rootDir.mkdirs();

    File dir = initializerProjectDir(rootDir, request);

    if (isGradleBuild(request)) {
      String gradle = new String(doGenerateGradleBuild(model));
      writeText(new File(dir, "build.gradle"), gradle);
      writeGradleWrapper(dir, Version.safeParse(request.getBootVersion()));
    } else {
      String pom = new String(doGenerateMavenPom(model));
      writeText(new File(dir, "pom.xml"), pom);
      writeMavenWrapper(dir);
    }

    generateGitIgnore(dir, request);

    String applicationName = request.getApplicationName();
    String language = request.getLanguage();

    String codeLocation = language;
    File src = new File(new File(dir, "src/main/" + codeLocation),
        request.getPackageName().replace(".", "/"));
    src.mkdirs();
    String extension = (KOTLIN.equals(language) ? "kt" : language);
    write(new File(src, applicationName + "." + extension),
        "Application." + extension, model);

    if ("war".equals(request.getPackaging())) {
      String fileName = "ServletInitializer." + extension;
      write(new File(src, fileName), fileName, model);
    }

    File test = new File(new File(dir, "src/test/" + codeLocation),
        request.getPackageName().replace(".", "/"));
    test.mkdirs();
    setupTestModel(request, model);
    write(new File(test, applicationName + "Tests." + extension),
        "ApplicationTests." + extension, model);

    writeCamelUsecases(request, model, dir, src, appProperties);

    File resources = new File(dir, "src/main/resources");
    resources.mkdirs();
    writeText(new File(resources, "application.yml"), appProperties.toString());

    if (request.hasWebFacet()) {
      new File(dir, "src/main/resources/templates").mkdirs();
      new File(dir, "src/main/resources/static").mkdirs();
    }
    return rootDir;
  }

  /**
   * Write routes, properties, and configuration for usecase facets e.g. file2sftp-usecase
   */
  private void writeCamelUsecases(ProjectRequest request, Map<String, Object> model, File root,
      File src,
      StringBuilder appProperties) {

    try {
      List<String> usecases = request.getUsecaseNames();
      appendProperties("classpath:templates/camel/properties/default.yml", appProperties);

      if (!usecases.isEmpty()) {
        for (String usecase : usecases) {
          appendFile(usecase + "Router.java", "camel/router/", src, model);
          appendFile(usecase + "Config.java", "camel/config/", src, model);
          appendFile(usecase + "Properties.java", "camel/properties/", src, model);
          appendFile("Dockerfile", "", root, model);
          appendFile("Readme.adoc", "", root, model);

          appendProperties("classpath:templates/camel/properties/" + usecase + ".yml",
              appProperties);
        }
      }
    } catch (IOException ex) {
      throw new InitializrException("Failure while processing Camel Usecases", ex);
    }
  }

  private void appendFile(String filename, String templateLocation, File srcDir,
      Map<String, Object> model) {
    try {
      write(new File(srcDir, filename),
          templateLocation + filename, model);
    } catch (IllegalStateException ex) {
      log.warn("Class file: {} not found: {} ", filename, ex.getMessage());
    }
  }

  private void appendProperties(String propertiesLocation, StringBuilder appProperties)
      throws IOException {
    try (InputStream custom = ResourceUtils
        .getURL(propertiesLocation).openStream()) {
      String customProperties = CharStreams.toString(
          new InputStreamReader(custom, Charsets.UTF_8));

      appProperties.append(customProperties).append(System.lineSeparator());
    } catch (FileNotFoundException ex) {
      log.warn("Property file not found: {}", ex.getMessage());
    }
  }


  /**
   * Create a distribution file for the specified project structure directory and extension
   */
  public File createDistributionFile(File dir, String extension) {
    File download = new File(getTemporaryDirectory(), dir.getName() + extension);
    addTempFile(dir.getName(), download);
    return download;
  }

  private File getTemporaryDirectory() {
    if (temporaryDirectory == null) {
      temporaryDirectory = new File(tmpdir, "initializr");
      temporaryDirectory.mkdirs();
    }
    return temporaryDirectory;
  }

  public void setTemporaryDirectory(File temporaryDirectory) {
    this.temporaryDirectory = temporaryDirectory;
  }

  /**
   * Clean all the temporary files that are related to this root directory.
   *
   * @see #createDistributionFile
   */
  public void cleanTempFiles(File dir) {
    List<File> tempFiles = temporaryFiles.remove(dir.getName());
    if (!tempFiles.isEmpty()) {
      tempFiles.forEach((File file) -> {
        if (file.isDirectory()) {
          FileSystemUtils.deleteRecursively(file);
        } else if (file.exists()) {
          file.delete();
        }
      });
    }
  }

  private void publishProjectGeneratedEvent(ProjectRequest request) {
    ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
    eventPublisher.publishEvent(event);
  }

  private void publishProjectFailedEvent(ProjectRequest request, Exception cause) {
    ProjectFailedEvent event = new ProjectFailedEvent(request, cause);
    eventPublisher.publishEvent(event);
  }

  /**
   * Generate a {@code .gitignore} file for the specified {@link ProjectRequest}
   *
   * @param dir the root directory of the project
   * @param request the request to handle
   */
  protected void generateGitIgnore(File dir, ProjectRequest request) {
    Map<String, Object> model = new LinkedHashMap<>();
    if (isMavenBuild(request)) {
      model.put("build", "maven");
      model.put("mavenBuild", true);
    } else {
      model.put("build", "gradle");
    }
    write(new File(dir, ".gitignore"), "gitignore.tmpl", model);
  }

  /**
   * Resolve the specified {@link ProjectRequest} and return the model to use to generate the
   * project
   *
   * @param originalRequest the request to handle
   * @return a model for that request
   */
  protected Map<String, Object> resolveModel(ProjectRequest originalRequest) {
    Assert.notNull(originalRequest.getBootVersion(), "boot version must not be null");
    Map<String, Object> model = new LinkedHashMap<>();
    InitializrMetadata metadata = metadataProvider.get();

    ProjectRequest request = requestResolver.resolve(originalRequest, metadata);

    // request resolved so we can log what has been requested
    Version bootVersion = Version.safeParse(request.getBootVersion());
    List<Dependency> dependencies = request.getResolvedDependencies();
    List<String> dependencyIds = dependencies.stream().map(Dependency::getId)
        .collect(Collectors.toList());
    log.info("Processing request{type=" + request.getType() + ", dependencies="
        + dependencyIds);

    if (isWar(request)) {
      model.put("war", true);
    }

    // Kotlin supported as of M6
    final boolean kotlinSupport = VERSION_2_0_0_M6
        .compareTo(bootVersion) <= 0;
    model.put("kotlinSupport", kotlinSupport);

    if (isMavenBuild(request)) {
      model.put("mavenBuild", true);
      ParentPom parentPom = metadata.getConfiguration().getEnv().getMaven()
          .resolveParentPom(request.getBootVersion());
      if (parentPom.isIncludeSpringBootBom()
          && !request.getBoms().containsKey("spring-boot")) {
        request.getBoms().put("spring-boot", metadata.createSpringBootBom(
            request.getBootVersion(), "spring-boot.version"));
      }

      model.put("mavenParentGroupId", parentPom.getGroupId());
      model.put("mavenParentArtifactId", parentPom.getArtifactId());
      model.put("mavenParentVersion", parentPom.getVersion());
      model.put("includeSpringBootBom", parentPom.isIncludeSpringBootBom());
    }

    model.put("repositoryValues", request.getRepositories().entrySet());
    if (!request.getRepositories().isEmpty()) {
      model.put("hasRepositories", true);
    }

    List<Map<String, String>> resolvedBoms = buildResolvedBoms(request);
    model.put("resolvedBoms", resolvedBoms);
    ArrayList<Map<String, String>> reversedBoms = new ArrayList<>(resolvedBoms);
    Collections.reverse(reversedBoms);
    model.put("reversedBoms", reversedBoms);

    model.put("compileDependencies",
        filterDependencies(dependencies, Dependency.SCOPE_COMPILE));
    model.put("runtimeDependencies",
        filterDependencies(dependencies, Dependency.SCOPE_RUNTIME));
    model.put("compileOnlyDependencies",
        filterDependencies(dependencies, Dependency.SCOPE_COMPILE_ONLY));
    model.put("providedDependencies",
        filterDependencies(dependencies, Dependency.SCOPE_PROVIDED));
    model.put("testDependencies",
        filterDependencies(dependencies, Dependency.SCOPE_TEST));

    request.getBoms().forEach((k, v) -> {
      if (v.getVersionProperty() != null) {
        request.getBuildProperties().getVersions().computeIfAbsent(
            v.getVersionProperty(), key -> v::getVersion);
      }
    });

    Map<String, String> versions = new LinkedHashMap<>();
    model.put("buildPropertiesVersions", versions.entrySet());
    request.getBuildProperties().getVersions().forEach((k, v) ->
        versions.put(computeVersionProperty(request, k), v.get()));
    Map<String, String> gradle = new LinkedHashMap<>();
    model.put("buildPropertiesGradle", gradle.entrySet());
    request.getBuildProperties().getGradle().forEach((k, v) ->
        gradle.put(k, v.get()));
    Map<String, String> maven = new LinkedHashMap<>();
    model.put("buildPropertiesMaven", maven.entrySet());
    request.getBuildProperties().getMaven().forEach((k, v) -> maven.put(k, v.get()));

    // Add various versions
    model.put("dependencyManagementPluginVersion", metadata.getConfiguration()
        .getEnv().getGradle().getDependencyManagementPluginVersion());
    model.put("kotlinVersion", metadata.getConfiguration().getEnv().getKotlin()
        .resolveKotlinVersion(bootVersion));
    if ("kotlin".equals(request.getLanguage())) {
      model.put("kotlin", true);
    }
    if ("groovy".equals(request.getLanguage())) {
      model.put("groovy", true);
    }

    model.put("isRelease", request.getBootVersion().contains("RELEASE"));
    setupApplicationModel(request, model);

    // Gradle plugin has changed as from 1.3.0
    model.put("bootOneThreeAvailable", VERSION_1_3_0_M1
        .compareTo(bootVersion) <= 0);

    model.put("bootTwoZeroAvailable", VERSION_2_0_0_M1
        .compareTo(bootVersion) <= 0);

    // Gradle plugin has changed again as from 1.4.2
    model.put("springBootPluginName",
        (VERSION_1_4_2_M1
            .compareTo(bootVersion) <= 0
            ? "org.springframework.boot" : "spring-boot"));

    // New testing stuff
    model.put("newTestInfrastructure", isNewTestInfrastructureAvailable(request));

    // Servlet Initializer
    model.put("servletInitializrImport", new Imports(request.getLanguage()).add(
        getServletInitializrClass(request)).toString());

    // Kotlin-specific dep
    model.put("kotlinStdlibArtifactId", getKotlinStdlibArtifactId(request));

    // Java versions
    model.put("java8OrLater", isJava8OrLater(request));

    // Append the project request to the model
    BeanWrapperImpl bean = new BeanWrapperImpl(request);
    for (PropertyDescriptor descriptor : bean.getPropertyDescriptors()) {
      if (bean.isReadableProperty(descriptor.getName())) {
        model.put(descriptor.getName(),
            bean.getPropertyValue(descriptor.getName()));
      }
    }
    if (!request.getBoms().isEmpty()) {
      model.put("hasBoms", true);
    }

    return model;
  }

  private List<Map<String, String>> buildResolvedBoms(ProjectRequest request) {
    return request.getBoms().values().stream()
        .sorted(Comparator.comparing(BillOfMaterials::getOrder))
        .map(bom -> toBomModel(request, bom))
        .collect(Collectors.toList());
  }

  private Map<String, String> toBomModel(ProjectRequest request, BillOfMaterials bom) {
    Map<String, String> model = new HashMap<>();
    model.put("groupId", bom.getGroupId());
    model.put("artifactId", bom.getArtifactId());
    model.put("versionToken", (bom.getVersionProperty() != null
        ? "${" + computeVersionProperty(request, bom.getVersionProperty()) + "}"
        : bom.getVersion()));
    return model;
  }

  private String computeVersionProperty(ProjectRequest request,
      VersionProperty property) {
    if (isGradleBuild(request)) {
      return property.toCamelCaseFormat();
    }
    return property.toStandardFormat();
  }

  protected void setupApplicationModel(ProjectRequest request,
      Map<String, Object> model) {
    Imports imports = new Imports(request.getLanguage());
    Annotations annotations = new Annotations();
    boolean useSpringBootApplication = VERSION_1_2_0_RC1
        .compareTo(Version.safeParse(request.getBootVersion())) <= 0;
    if (useSpringBootApplication) {
      imports.add("org.springframework.boot.autoconfigure.SpringBootApplication");
      annotations.add("@SpringBootApplication");
    } else {
      imports.add("org.springframework.boot.autoconfigure.EnableAutoConfiguration")
          .add("org.springframework.context.annotation.ComponentScan")
          .add("org.springframework.context.annotation.Configuration");
      annotations.add("@EnableAutoConfiguration")
          .add("@ComponentScan")
          .add("@Configuration");
    }
    model.put("applicationImports", imports.toString());
    model.put("applicationAnnotations", annotations.toString());
    model.put("license", LICENSE);


  }

  protected void setupTestModel(ProjectRequest request, Map<String, Object> model) {
    Imports imports = new Imports(request.getLanguage());
    Annotations testAnnotations = new Annotations();
    boolean newTestInfrastructure = isNewTestInfrastructureAvailable(request);
    if (newTestInfrastructure) {
      imports.add("org.springframework.boot.test.context.SpringBootTest")
          .add("org.springframework.test.context.junit4.SpringRunner");
    } else {
      imports.add("org.springframework.boot.test.SpringApplicationConfiguration")
          .add("org.springframework.test.context.junit4.SpringJUnit4ClassRunner");
    }
    if (request.hasWebFacet() && !newTestInfrastructure) {
      imports.add("org.springframework.test.context.web.WebAppConfiguration");
      testAnnotations.add("@WebAppConfiguration");
    }
    model.put("testImports", imports.withFinalCarriageReturn().toString());
    model.put("testAnnotations", testAnnotations.withFinalCarriageReturn().toString());
  }

  protected String getServletInitializrClass(ProjectRequest request) {
    Version bootVersion = Version.safeParse(request.getBootVersion());
    if (VERSION_1_4_0_M3.compareTo(bootVersion) > 0) {
      return "org.springframework.boot.context.web.SpringBootServletInitializer";
    } else if (VERSION_2_0_0_M1.compareTo(bootVersion) > 0) {
      return "org.springframework.boot.web.support.SpringBootServletInitializer";
    } else {
      return "org.springframework.boot.web.servlet.support.SpringBootServletInitializer";
    }
  }

  protected String getKotlinStdlibArtifactId(ProjectRequest request) {
    String javaVersion = request.getJavaVersion();
    if ("1.6".equals(javaVersion)) {
      return "kotlin-stdlib";
    } else if ("1.7".equals(javaVersion)) {
      return "kotlin-stdlib-jdk7";
    }
    return "kotlin-stdlib-jdk8";
  }

  private byte[] doGenerateMavenPom(Map<String, Object> model) {
    return templateRenderer.process("starter-pom.xml", model).getBytes();
  }

  private byte[] doGenerateGradleBuild(Map<String, Object> model) {
    return templateRenderer.process("starter-build.gradle", model).getBytes();
  }

  private void writeGradleWrapper(File dir, Version bootVersion) {
    String gradlePrefix = isGradle4Available(bootVersion) ? "gradle4" :
        isGradle3Available(bootVersion) ? "gradle3" : "gradle";
    writeTextResource(dir, "gradlew.bat", gradlePrefix + "/gradlew.bat");
    writeTextResource(dir, "gradlew", gradlePrefix + "/gradlew");

    File wrapperDir = new File(dir, "gradle/wrapper");
    wrapperDir.mkdirs();
    writeTextResource(wrapperDir, "gradle-wrapper.properties",
        gradlePrefix + "/gradle/wrapper/gradle-wrapper.properties");
    writeBinaryResource(wrapperDir, "gradle-wrapper.jar",
        gradlePrefix + "/gradle/wrapper/gradle-wrapper.jar");
  }

  private void writeMavenWrapper(File dir) {
    writeTextResource(dir, "mvnw.cmd", "maven/mvnw.cmd");
    writeTextResource(dir, "mvnw", "maven/mvnw");

    File wrapperDir = new File(dir, ".mvn/wrapper");
    wrapperDir.mkdirs();
    writeTextResource(wrapperDir, "maven-wrapper.properties",
        "maven/wrapper/maven-wrapper.properties");
    writeBinaryResource(wrapperDir, "maven-wrapper.jar",
        "maven/wrapper/maven-wrapper.jar");
  }

  private File writeBinaryResource(File dir, String name, String location) {
    return doWriteProjectResource(dir, name, location, true);
  }

  private File writeTextResource(File dir, String name, String location) {
    return doWriteProjectResource(dir, name, location, false);
  }

  private File doWriteProjectResource(File dir, String name, String location,
      boolean binary) {
    File target = new File(dir, name);
    if (binary) {
      writeBinary(target, projectResourceLocator
          .getBinaryResource("classpath:project/" + location));
    } else {
      writeText(target, projectResourceLocator
          .getTextResource("classpath:project/" + location));
    }
    return target;
  }

  private File initializerProjectDir(File rootDir, ProjectRequest request) {
    if (request.getBaseDir() != null) {
      File dir = new File(rootDir, request.getBaseDir());
      dir.mkdirs();
      return dir;
    } else {
      return rootDir;
    }
  }

  public void write(File target, String templateName, Map<String, Object> model) {
    String body = templateRenderer.process(templateName, model);
    writeText(target, body);
  }

  private void writeText(File target, String body) {
    try (OutputStream stream = new FileOutputStream(target)) {
      StreamUtils.copy(body, Charset.forName("UTF-8"), stream);
    } catch (Exception e) {
      throw new IllegalStateException("Cannot write file " + target, e);
    }
  }

  private void writeBinary(File target, byte[] body) {
    try (OutputStream stream = new FileOutputStream(target)) {
      StreamUtils.copy(body, stream);
    } catch (Exception e) {
      throw new IllegalStateException("Cannot write file " + target, e);
    }
  }

  private void addTempFile(String group, File file) {
    temporaryFiles.computeIfAbsent(group, key -> new ArrayList<>()).add(file);
  }

  private static class DependencyComparator implements Comparator<Dependency> {

    private static final DependencyComparator INSTANCE = new DependencyComparator();

    @Override
    public int compare(Dependency o1, Dependency o2) {
      if (isSpringBootDependency(o1) && isSpringBootDependency(o2)) {
        return o1.getArtifactId().compareTo(o2.getArtifactId());
      }
      if (isSpringBootDependency(o1)) {
        return -1;
      }
      if (isSpringBootDependency(o2)) {
        return 1;
      }
      int group = o1.getGroupId().compareTo(o2.getGroupId());
      if (group != 0) {
        return group;
      }
      return o1.getArtifactId().compareTo(o2.getArtifactId());
    }

    private boolean isSpringBootDependency(Dependency dependency) {
      return dependency.getGroupId().startsWith("org.springframework.boot");
    }

  }

  private static class Imports {

    private final List<String> statements = new ArrayList<>();
    private final String language;
    private boolean finalCarriageReturn;

    public Imports(String language) {
      this.language = language;
    }

    public Imports add(String type) {
      this.statements.add(generateImport(type, language));
      return this;
    }

    public Imports withFinalCarriageReturn() {
      this.finalCarriageReturn = true;
      return this;
    }

    private String generateImport(String type, String language) {
      String end = ("groovy".equals(language) || "kotlin".equals(language)) ? "" : ";";
      return "import " + type + end;
    }

    public String toString() {
      if (this.statements.isEmpty()) {
        return "";
      }
      String content = String.join(String.format("%n"), this.statements);
      return (finalCarriageReturn ? String.format("%s%n", content) : content);
    }

  }

  private static class Annotations {

    private final List<String> statements = new ArrayList<>();
    private boolean finalCarriageReturn;

    public Annotations add(String type) {
      this.statements.add(type);
      return this;
    }

    public Annotations withFinalCarriageReturn() {
      this.finalCarriageReturn = true;
      return this;
    }

    public String toString() {
      if (this.statements.isEmpty()) {
        return "";
      }
      String content = String.join(String.format("%n"), this.statements);
      return (finalCarriageReturn ? String.format("%s%n", content) : content);
    }

  }

}

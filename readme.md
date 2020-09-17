## 本文章仅作为个人笔记
#### 官方[网站](https://swagger.io/) / 参考[文档](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md) / 官方[项目](https://github.com/swagger-api/swagger-core) / 个人 [demo]()
* wagger 的集成与静态文件生成 (在线/离线文档)
    * 新建springboot项目并在build.gradle文件添加相关依赖

            # 引用插件(不生成离线文档可以不引入)
            plugins {
                id 'org.asciidoctor.convert' version '2.3.0'
                id "io.github.lhotari.swagger2markup" version "1.3.3.1"
            }
            # 解决中文乱码问题
            tasks.withType(JavaCompile) {
                options.deprecation = true
                options.encoding = 'UTF-8'
                options.compilerArgs << "-Xlint:unchecked"
            }
            # 设置仓库
            repositories {
                jcenter()
                mavenCentral()
            }
            # 设置输出文件夹及swagger版本
            ext {
                swaggerVersion = "3.0.0"
                swaggerOutputDir = file("${project.rootDir}/api/swagger")
                asciiDocOutputDir = file("${project.rootDir}/api/asciidoc")
                snippetsOutputDir = file("${project.rootDir}/api/snippets")
            }
            # 引用依赖
            dependencies {
                // swagger3 依赖
                implementation("io.springfox:springfox-boot-starter:${swaggerVersion}")
                // 离线文档生成 依赖(不生成离线文档可以不引入)
                implementation("io.github.swagger2markup:swagger2markup:1.3.3")
                // 测试 依赖(不生成离线文档可以不引入)
                testImplementation("org.springframework.boot:spring-boot-starter-test")
                testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
            }
            # 设置全局离线文档输出地址 (不生成离线文档可以不设置)
            test {
                systemProperty 'io.springfox.staticdocs.outputDir', swaggerOutputDir
                systemProperty 'io.springfox.staticdocs.snippetsOutputDir', snippetsOutputDir
                # 构建时忽略 生成文件的测试
                exclude '**/DemoApplicationTests.class'
            }
            # 添加离线文档转换task (不生成离线文档可以不设置)
            convertSwagger2markup {
                dependsOn test
                swaggerInput "${swaggerOutputDir}/swagger.json"
                outputDir asciiDocOutputDir
                config = [
                        'swagger2markup.pathsGroupedBy'                          : 'TAGS',
                        'swagger2markup.extensions.springRestDocs.snippetBaseUri': snippetsOutputDir.getAbsolutePath()]
            }
            # 添加离线文档生成task (不生成离线文档可以不设置)
            asciidoctor {
                dependsOn convertSwagger2markup
                sourceDir = file("${swaggerOutputDir}")
                sources {
                    include 'index.adoc'
                }
                backends = ['html5']
                attributes = [
                        doctype    : 'book',
                        toc        : 'left',
                        toclevels  : '3',
                        numbered   : '',
                        sectlinks  : '',
                        sectanchors: '',
                        hardbreaks : '',
                        generated  : swaggerOutputDir
                ]
                outputDir = swaggerOutputDir
            }

    * 为项目添加 swagger 配置 开启swagger/设置swagger基本信息等
    
            # 自定义swagger开关 在 application.yml 添加对应配置打开或者不设置便关闭
            @Value("${swagger.enable:false}")
            private boolean swaggerEnable;
            @Bean
            public Docket createRestApi() {
                return new Docket(DocumentationType.OAS_30)
                        .enable(swaggerEnable)
                        .apiInfo(apiInfo())
                        .select()
                        .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                        .paths(PathSelectors.any())
                        .build();
            }
            private ApiInfo apiInfo() {
                return new ApiInfoBuilder()
                        .title("图模服务")
                        .version("1.0")
                        .build();
            }
    
    * 为项目添加单元测试，用于生成静态  adoc/md/json 文件(不生成静态文件请忽略)
        
            # 获取设置的静态文件生成路径
            String outputDir = System.getProperty("io.springfox.staticdocs.outputDir");
            # 根据api获取接口内容
            MvcResult mvcResult = this.mockMvc
                    .perform(get("/v2/api-docs")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
            MockHttpServletResponse response = mvcResult.getResponse();
            response.setCharacterEncoding("utf-8");
            String swaggerJson = response.getContentAsString();
            Files.createDirectories(Paths.get(outputDir));
            # 输出为 json 文件
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputDir,
                    "swagger.json"), StandardCharsets.UTF_8)) {
                writer.write(swaggerJson);
            }
            Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder()
                    .withMarkupLanguage(MarkupLanguage.ASCIIDOC)
                    .withOutputLanguage(Language.ZH)
                    .withPathsGroupedBy(GroupBy.TAGS)
                    .withGeneratedExamples()
                    .withoutInlineSchema()
                    .build();
            # 输出为 asciidoc 文件
            Swagger2MarkupConverter.from(swaggerJson)
                    .withConfig(config)
                    .build()
                    .toFile(Paths.get(outputDir + "/index"));
            config = new Swagger2MarkupConfigBuilder()
                    .withMarkupLanguage(MarkupLanguage.MARKDOWN)
                    .withOutputLanguage(Language.ZH)
                    .withPathsGroupedBy(GroupBy.TAGS)
                    .withGeneratedExamples()
                    .withoutInlineSchema()
                    .build();
            # 输出为 md 文件
            Swagger2MarkupConverter.from(swaggerJson)
                    .withConfig(config)
                    .build()
                    .toFile(Paths.get(outputDir + "/index"));
    
    * 做好准备工作为相应的接口(api/controller)添加对应swagger注解 具体用法自行参考官方文档
    * 最后运行 
        
            gradle build test
            gradle build asciidoctor
    
    * 至此便完成了 swagger3 的集成与静态文件的生成
    

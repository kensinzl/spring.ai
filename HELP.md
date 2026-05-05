# Getting Started

### OpenAI platform
General -> API Keys -> [Create API Key](https://platform.openai.com/settings/organization/api-keys)
Billing -> See each model pricing
Github code -> [Examples](https://github.com/eazybytes/spring-ai)
H2 database console -> [Console](http://localhost:8080/h2-console) and [store path](/Users/myname)
Qdrant -> [Qdrant Local](http://localhost:6333/dashboard#/datasets)


### Qdrant
docker compose up

### MCP X-ray tool
https://github.com/modelcontextprotocol/inspector

`npx @modelcontextprotocol/inspector`

### IntelliJ
Command + Shift + O




For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.5.10/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.5.10/maven-plugin/build-image.html)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/3.5.10/reference/using/devtools.html)
* [Spring Web](https://docs.spring.io/spring-boot/3.5.10/reference/web/servlet.html)
* [OpenAI](https://docs.spring.io/spring-ai/reference/api/chat/openai-chat.html)
* [MCP](https://github.com/modelcontextprotocol/servers)
* [Vector embeddings](https://developers.openai.com/api/docs/guides/embeddings)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.


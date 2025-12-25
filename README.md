# AssignedHeapMemory - 商品マスター管理サンプル

シンプルなSpring Boot（Thymeleaf）アプリケーション。データはテキストファイル（`data/products.txt`）に保存されます。

ビルドと実行:

```bash
mvn clean package
mvn spring-boot:run
```

または生成されたWARを実行:

```bash
mvn package
java -jar target/assigned-heap-memory-0.0.1-SNAPSHOT.war
```

アクセス: http://localhost:8080/products

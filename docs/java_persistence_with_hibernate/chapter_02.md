Created: 2015-06-02
Pages: 72 - 139 (67)


# Chapter 2 - Starting a project

A tour through a straightforward "Hello World" application.

In this chapter, you’ll learn how to set up a project infrastructure for a plain Java application that integrates Hibernate, and you’ll see many more details about how Hibernate can be configured in such an environment. We also discuss configuration and integration of Hibernate in a managed environment—that is, an environment that provides Java EE services.

As a build tool for the "Hello World" project, we introduce Ant and create build scripts that can not only compile and run the project, but also utilize the Hibernate Tools. Depending on your development process, you’ll use the Hibernate toolset to export database schemas automatically or even to reverse-engineer a complete application from an existing (legacy) database schema.



## 2.1 Starting a Hibernate project

This is your road map:

1. Select a development process
2. Set up the project infrastructure
3. Write application code and mappings
4. Configure and start Hibernate
5. Run the application.

Import and export tasks for Ant: 
- hbm2java (Persistent Class or Annotations, Java Source)
- hbm2dao (Data Access Object, Java Source)
- hbm2doc (Documentation, HTML)
- hbm2ddl (Database Schema)
- hbm2hbmxml (Mapping Metadata, XML)
- hbm2cfgxml (Configuration, XML)
- hbmtemplate (Freemarker Template)

The following development scenarios are common:

- top down

In top-down development, you start with an existing domain model, its implementation in Java, and (ideally) complete freedom with respect to the database schema. You must create mapping metadata - either with XML files or by annotating the Java source — and then optionally let Hibernate’s hbm2ddl tool generate the database schema. In the absence of an existing database schema, this is the most comfortable development style for most Java developers. You may even use the Hibernate Tools to automatically refresh the database schema on every application restart in development.

- bottom up

Conversely, bottom-up development begins with an existing database schema and data model. In this case, the easiest way to proceed is to use the reverse-engineering tools to extract metadata from the database. This metadata can be used to generate XML mapping files, with hbm2hbmxml for example. With hbm2java, the Hibernate mapping metadata is used to generate Java persistent classes, and even data access objects—in other words, a skeleton for a Java persistence layer. Or, instead of writing to XML mapping files, annotated Java source code (EJB 3.0 entity classes) can be produced directly by the tools. However, not all class association details and Java-specific metainformation can be automatically generated from an SQL database schema with this strategy, so expect some manual work.

- middle out

The Hibernate XML mapping metadata provides sufficient information to completely deduce the database schema and to generate the Java source code for the persistence layer of the application. Furthermore,
the XML mapping document isn’t too verbose. Hence, some architects and developers prefer middle-out development, where they begin with handwritten Hibernate XML mapping files, and then generate the database schema using hbm2ddl and Java classes using hbm2java. The Hibernate XML mapping files are constantly updated during development, and other artifacts are generated from this master definition. Additional business logic or database objects are added through subclassing and auxiliary DDL. This development style can be recommended only for the seasoned Hibernate expert.

- meet in the middle

The most difficult scenario is combining existing Java classes and an existing database schema. In this case, there is little that the Hibernate toolset can do to help. It is, of course, not possible to map arbitrary Java domain models to a given schema, so this scenario usually requires at least some refactoring of the Java classes, database schema, or both. The mapping metadata will almost certainly need to be written by hand and in XML files (though it might be possible to use annotations if there is a close match). This can be an incredibly painful scenario, and it is, fortunately, exceedingly rare.


The development process we assume first is top down, and we’ll walk through a Hibernate project that doesn’t involve any legacy data schemas or Java code. After that, you’ll migrate the code to JPA and EJB 3.0, and then you’ll start a project bottom up by reverse-engineering from an existing database schema.

### 2.1.2 Setting up the project
- Database
- jdbc driver
- hibernate

=======================================================

!!! Какие библиотеки нужно подключать в мавене
http://docs.jboss.org/hibernate/orm/4.3/manual/en-US/html/ch01.html#tutorial-firstapp-setup

- Hibernate configuration file (hibernate.cfg.xml)
- HibernateUtil.java file
- Persistent class (for example, Message.java)
- The mapping file (for example, Message.hbm.xml)

=========================================================

### Creating the domain model (persistent classes)

Hibernate applications define persistent classes that are mapped to database tables. You define these classes based on your analysis of the business domain; hence, they’re a model of the domain.

The identifier attribute allows the application to access the database identity — the primary key value — of a persistent object. If two instances of Message have the same identifier value, they represent the same row in the database.

The no-argument constructor is a requirement (tools like Hibernate use reflection on this constructor to instantiate objects).

Instances of the Message class can be managed (made persistent) by Hibernate, but they don’t have to be. Because the Message object doesn’t implement any Hibernate-specific classes or interfaces, you can use it just like any other Java class.

The persistent class can be used in any execution context at all—no special container is needed. Note that this is also one of the benefits of the new JPA entities, which are also plain Java objects.


### Mapping the class to a database schema (XML mapping document)

To allow the object/relational mapping magic to occur, Hibernate needs some more information about exactly how the Message class should be made persistent. In other words, Hibernate needs to know how instances of that class are supposed to be stored and loaded. This metadata can be written into an XML mapping document, which defines, among other things, how properties of the Message class map to columns of a MESSAGES table.

Later, we discuss a way of using annotations directly in the source code to define mapping information; but whichever method you choose, Hibernate has enough information to generate all the SQL statements needed to insert, update, delete, and retrieve instances of the Message class. You no longer need to write these SQL statements by hand.

The hbm suffix (Message.hbm.xml) is a naming convention accepted by the Hibernate community, and most developers prefer to place mapping files next to the source code of their domain classes.


### Storing and loading objects

You call the Hibernate Session, Transaction, and Query interfaces to access the database:

- org.hibernate.Session 
A Hibernate Session is many things in one. It’s a single-threaded nonshared object that represents a particular unit of work with the database. It has the persistence manager API you call to load and store objects. (The Session internals consist of a queue of SQL statements that need to be synchronized with the database at some point and a map of managed persistence instances that are monitored by the Session.)

- org.hibernate.Transaction
This Hibernate API can be used to set transaction boundaries programmatically, but it’s optional (transaction boundaries aren’t). Other choices are JDBC transaction demarcation, the JTA interface, or container-managed transactions with EJBs.

- org.hibernate.Query
A database query can be written in Hibernate’s own object-oriented query language (HQL) or plain SQL. This interface allows you to create queries, bind arguments to placeholders in the query, and execute the query in various ways.

The id property is special. It’s an identifier property: It holds a generated unique value. The value is assigned to the Message instance by Hibernate when save() is called.

All SQL is generated at runtime (actually, at startup for all reusable SQL statements).

Two other Hibernate features—automatic dirty checking and cascading.

Atomatic dirty checking saves you the effort of explicitly asking Hibernate to update the database when you modify the state of an object inside a unit of work.

Similarly, the new message was made persistent when a reference was created from the first message. This feature is called cascading save. It saves you the effort of explicitly making the new object persistent by calling save(), as long as it’s reachable by an already persistent instance.

Also notice that the ordering of the SQL statements isn’t the same as the order in which you set property values. Hibernate uses a sophisticated algorithm to determine an efficient ordering that avoids database foreign key constraint violations but is still sufficiently predictable to the user. This feature is called transactional write-behind.


### 2.1.3 Hibernate configuration and startup
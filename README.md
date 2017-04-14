# Spring JdbcTemplate for Apache Phoenix with Java 8 Streams

The goals of the code are to offer the expressiveness of a Stream interface, without losing the resource management capabilities of JdbcTemplate or loading an entire result set into memory before processing it.

The code snippet below shows how the final implementation can be used.

In this case, the SQL statement itself could easily have done the filter expression, and could have done a DISTINCT select. But often the transformations to be done on an SQL query’s output are either difficult to do in SQL, or would require generation of moderately complex SQL on the fly.

The end result is a simple way to use the Java 8 Stream API on SQL results in Spring Framework.

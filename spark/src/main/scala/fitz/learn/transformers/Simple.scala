package fitz.learn.transformers

import org.apache.spark.sql.SparkSession

object Simple {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("simple")
      .master("local")
      .getOrCreate()

  }
}
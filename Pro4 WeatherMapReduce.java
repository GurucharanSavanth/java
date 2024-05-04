import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
@SuppressWarnings("unused")
public class WeatherMapReduce {
public static class WeatherMapper
extends Mapper<LongWritable, Text, Text, DoubleWritable>{
private final static DoubleWritable temperature = new DoubleWritable();
private Text date = new Text();
public void map(LongWritable key, Text value, Context context
) throws IOException, InterruptedException {
String[] line = value.toString().split(",");
if (line.length == 3) {
date.set(line[0]);
temperature.set(Double.parseDouble(line[2]));
context.write(date, temperature);
}
}
}

public static class WeatherReducer
extends Reducer<Text,DoubleWritable,Text,DoubleWritable> {
private DoubleWritable result = new DoubleWritable();
public void reduce(Text key, Iterable<DoubleWritable> values,
Context context
) throws IOException, InterruptedException {
double sum = 0;
int count = 0;
for (DoubleWritable val : values) {
sum += val.get();
count++;
}
double avg = sum / count;
result.set(avg);
context.write(key, result);
}
}
public static void main(String[] args) throws Exception {
Configuration conf = new Configuration();
Job job = Job.getInstance(conf, "weather analysis");
job.setJarByClass(WeatherMapReduce.class);
job.setMapperClass(WeatherMapper.class);
job.setReducerClass(WeatherReducer.class);
job.setOutputKeyClass(Text.class);
job.setOutputValueClass(DoubleWritable.class);
FileInputFormat.setInputPaths(job, new Path("input.txt"));
FileOutputFormat.setOutputPath(job, new Path("output"));
System.exit(job.waitForCompletion(true)?0 : 1);
}
}

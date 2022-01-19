package rest;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface IDocumentReader {

	public <T extends Enum<?>> T readEnum(String field, Class<T> cls);

	public <T> T readEmbedded(String field, String type, T exists);

	public List<Long> readLongColl(String field);

	public List<String> readStringColl(String field);

	public <T> List<T> readChildColl(String field, String type);

	public <T> List<T> readUnionColl(String field, String type);

	public <T extends Enum<?>> List<T> readEnumColl(String field, Class<T> cls);

	public <T> T readChild(String field, String type);

	public <T> T readUnion(String field, String type);

	public long readLong(String field);

	public String readString(String field);

	public boolean has(String field);

	public long readInteger(String field);

	public double readDouble(String field);

	public boolean readBoolean(String field);

	public Duration readDuration(String field);

	public LocalDateTime readDateTime(String field);

	public LocalTime readTime(String field);

	public <T extends Enum<?>> void writeEnum(String field, T val, T def);

	public <T> void writeEmbedded(String field, T exists);

	public void writeLongColl(String field, List<Long> coll);

	public void writeStringColl(String field, List<String> coll);

	public <T> void writeChildColl(String field, List<T> coll, String type);

	public <T> void writeUnionColl(String field, List<T> coll);

	public <T extends Enum<?>> void writeEnumColl(String field, List<T> coll);

	public <T> void writeChild(String field, T obj, String type);

	public <T> void writeUnion(String field, T obj);

	public void writeLong(String field, long val, long def);

	public void writeString(String field, String val, String def);

	public void writeInteger(String field, long val, long def);

	public void writeDouble(String field, double val, double def);

	public void writeBoolean(String field, boolean val, boolean def);

	public void writeDuration(String field, Duration val);

	public void writeDateTime(String field, LocalDateTime val);

	public void writeTime(String field, LocalTime val);
	
	public void writeDate(String field, LocalDate val);
}

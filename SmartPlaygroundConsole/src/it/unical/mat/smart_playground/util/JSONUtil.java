/**
 * 
 */
package it.unical.mat.smart_playground.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author Agostino
 *
 */
public class JSONUtil
{
	public static final FieldNamingPolicy FIELD_NAMING_POLICY = FieldNamingPolicy.UPPER_CAMEL_CASE;
	public static final String DATE_FORMAT_PATTERN = "dd MMM yyyy";
	
	public static <T> T readClassFromFile( final String path, final Class<T> ans_class )
	{	
		try
		{ return readClassFromReader( new FileReader(path), ans_class ); }
		catch (FileNotFoundException e)
		{ e.printStackTrace(); return null; }
	}
	
	public static <T> T readClassFromReader( final Reader reader, final Class<T> ans_class )
	{
		final GsonBuilder builder = new GsonBuilder();
		builder.setFieldNamingPolicy(FIELD_NAMING_POLICY).create();
		builder.setDateFormat(DATE_FORMAT_PATTERN);
		
		final Gson gson = builder.create();
		
		BufferedReader br = new BufferedReader( reader );
		return gson.fromJson(br, ans_class);
	}
	
	public static String fromListToString( final List<?> lst )
	{
		return new Gson().toJson(lst);
	}
	
	public static String toJson( final Object obj )
	{
		return new Gson().toJson(obj, obj.getClass());
	}
	
	public static JsonObject readFromReader( final BufferedReader reader )
	{
		return new Gson().fromJson(reader, JsonObject.class);
	}
	
	public static JsonArray fromStringToJsonArray( final String json )
	{
		return (JsonArray) JsonParser.parseString(json);
	}
	
	public static JsonObject fromStringToJsonObject( final String json )
	{
		return (JsonObject) JsonParser.parseString(json);
	}
	
	public static JsonArray fromListToJsonArray( final List<?> lst, final Class<?> itemsClass )
	{
		final Gson gson = new Gson();
		final JsonArray array = new JsonArray();
		for ( final Object item : lst )
			array.add( gson.toJsonTree(item) );
		return array;
	}
	
	public static List<Integer> fromJsonArrayToIntegerList( final JsonArray array )
	{
		final List<Integer> list = new ArrayList<>();
		for ( final JsonElement elem : array )
			list.add(elem.getAsInt());
		return list;
	}
}

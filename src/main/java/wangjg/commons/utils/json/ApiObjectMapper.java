package wangjg.commons.utils.json;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

/**
 * API专用,用于序列化JSON对象
 * 本项目所有的json使用此对象序列化和反序列化
 * @author wangjg
 */
public class ApiObjectMapper extends ObjectMapper {
	private static final long serialVersionUID = 1L;
	
	 static final String INCLUDE_FILTER = "allowedFields";
	 static final String EXCLUDE_FILTER = "disallowedFields";
	 
	 @JsonFilter(EXCLUDE_FILTER)
	 interface DynamicExclude {
	 }

	 @JsonFilter(INCLUDE_FILTER)
	 interface DynamicInclude {
	 }

	public ApiObjectMapper() {
		this.config1();
	}

	private void config1() {
		this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		this.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		this.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
//		this.configure(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS, true);
	}
	
	protected void register(Class clazz, JsonDeserializer des, JsonSerializer ser){
		SimpleModule module = new SimpleModule();
		module.addDeserializer(clazz, des); 
		module.addSerializer(clazz, ser);
		this.registerModule(module);
	}

	public void setDateFormat(String dateFormat) {
		if(StringUtils.isNotBlank(dateFormat)){
			DateFormat df = new SimpleDateFormat(dateFormat);
			super.setDateFormat(df);
		}else{
			super.setDateFormat(null);
		}
	}

	public void setIgnoreNull(boolean ignoreNull) {
		if(ignoreNull){
			this.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			this.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
		}else{
			this.setSerializationInclusion(JsonInclude.Include.ALWAYS);
			this.enable(SerializationFeature.WRITE_NULL_MAP_VALUES);
		}
	}

	public void setUndefined(boolean undefined) {
		final String nullStr = undefined ? "" : "null";
		JsonSerializer<Object> nullVs = new JsonSerializer<Object>(){
			public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider) throws IOException,JsonProcessingException {
				jgen.writeRawValue(nullStr);
			}
			
		};
		this.getSerializerProvider().setNullValueSerializer(nullVs);
	}

	public void setBeautifier(boolean beautifier) {
		if(beautifier){
			this.enable(SerializationFeature.INDENT_OUTPUT);
		}else{
			this.disable(SerializationFeature.INDENT_OUTPUT);
		}
	}

	public void setAllowedFields(Class clazz, String...allowedFields) {
		if(allowedFields!=null && allowedFields.length>0){
			SimpleBeanPropertyFilter filter = null ;
			filter = SimpleBeanPropertyFilter.filterOutAllExcept(allowedFields);
			FilterProvider filterProvider = new SimpleFilterProvider().addFilter(INCLUDE_FILTER, filter);  
			this.setFilterProvider(filterProvider);
			//两个类的注解混合，让第一个参数的类能够拥有第二个参数类的注解。
			this.addMixIn(clazz, DynamicInclude.class);
		}
	}

	public void setDisallowedFields(Class clazz, String...disallowedFields) {
		if(disallowedFields!=null && disallowedFields.length>0){
			SimpleBeanPropertyFilter excludeFilter = SimpleBeanPropertyFilter.serializeAllExcept(disallowedFields);
			FilterProvider filterProvider = new SimpleFilterProvider().addFilter(EXCLUDE_FILTER, excludeFilter);  
			this.setFilterProvider(filterProvider);
			//两个类的注解混合，让第一个参数的类能够拥有第二个参数类的注解。
		    this.addMixIn(clazz, DynamicExclude.class);
		}
	}

}
package com.hori.lxjsdk.component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.hori.lxjsdk.web.vo.ImagePathVo;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 文件或图片上传fms系统工具
 * @author laizs
 * @time 2017年8月8日下午1:42:37
 *
 */
@Component("fmsUploadUtil")
@Scope("singleton")
public class FmsUploadUtil {
	/**
	 * fms服务器地址,从配置文件获取
	 */
	@Value(value="${fms_server_address}")
	private String fmsServerAddress;
	/**
     * 将图片上传到图片服务器 ，返回图片路径
     * 注意上传的File类型，必须是图片 ，接文件后缀必须是图片类型后缀，否则fps生成缩略图时会出错
     * 
     * @param imgs 上传图片file数组，可上传多张
     * @param serverUrl fms 服务器图片上传接口地址
     * @return 返回响应  
     * @throws HttpException
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
	public  Map transImgToFms(File[] imgs) throws HttpException, IOException {
    	//文件上传接口
    	String imgUploadUrl= fmsServerAddress+"/imagesUpload";
    	Map result=new JSONObject();
    	result.put("result", "false");
    	List<Part> list=new ArrayList<Part>();
    	for(File img:imgs){
    		list.add(new FilePart(img.getName(),img));
    	}
    	Part[] parts=new Part[list.size()];
    	list.toArray(parts);
		PostMethod filePost = new PostMethod(imgUploadUrl);
		filePost.setRequestEntity(new MultipartRequestEntity(parts,filePost.getParams()));
		HttpClient client = new HttpClient();
		client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
		int status = client.executeMethod(filePost);
		if (status == HttpStatus.SC_OK) {
			//logger.info("上传成功");
			String res=filePost.getResponseBodyAsString();
			JSONObject json=JSONObject.fromObject(res);
			String resultVal=(String) json.get("result");
			if(StringUtils.isNotBlank(resultVal)&&"0".equals(resultVal)){//图片上传到fms成功的响应
				result.put("result", "success");
				List<ImagePathVo> imagePaths=JSONArray.toList(JSONArray.fromObject(json.get("list")), ImagePathVo.class);
				result.put("list", imagePaths);
			}
			
		} else {
		}
		return result;
    }
	
}

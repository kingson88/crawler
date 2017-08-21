package com.joniluo.crawler.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.joniluo.crawler.http.HttpUtil;



public class TmallItemHttp implements Runnable{

	private ItemInfo itemInfo;
	public TmallItemHttp(ItemInfo itemInfo){
		this.itemInfo=itemInfo;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//ShopItemHttpExcutor.getInstance().submit(command);
		long beginTime = System.currentTimeMillis();//得到线程绑定的局部变量（开始时间）  
		try{ 


			String html=getItemInfoFromUrl(itemInfo);			
			if(StringUtil.isNotEmptyStr(html)){
				//解析线程池
				getItemInfoFromHtml(html);
			}
			
		}catch(Exception ex){
			
		}
		long endTime=	System.currentTimeMillis();
		System.out.println(String.format("Execute TmallItemHttp!ok consume %s ms", endTime - beginTime)); 
	}
	
		
	private static String getItemInfoFromUrl(ItemInfo itemInfo){
		String result="";
		try{
			String urlEncoding="UTF-8";
			long beginTime=System.currentTimeMillis();
			result =HttpUtil.doGet(itemInfo.getItemUrl());
			if(StringUtil.isBlank(result)){
				result =HttpUtil.doGet(itemInfo.getItemUrl());
				if(StringUtil.isBlank(result)){
					result =HttpUtil.doGet(itemInfo.getItemUrl());
				}
			}
			long endTime=	System.currentTimeMillis();
			System.out.println(String.format("Execute getItemInfoFromUrl !ok consume %s ms  url %s", endTime - beginTime,itemInfo.getItemUrl()));
		}catch(Exception ex){
			
		}
		return result;
	}
	
	
	private void getItemInfoFromHtml(String html){

		try{
			//long beginTime=	System.currentTimeMillis();
			
			if(html.indexOf("您查看的商品找不到了")>-1){
				//商品已经删除
				
				return;
			}
			
			Document doc = Jsoup.parse(html);
			JSONObject json =InitTshopJson(html);
			
			getItemInfo(doc,json);	
			getRateTotal(itemInfo);
			getPromit(itemInfo);		
			getShopInfo(doc,json);
			
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private void getShopInfo(Document doc,JSONObject json){
		//<textarea class="ks-datalazyload">
		String shopName="";
		Integer ageNum=0;
		String prov="";
		String city="";
		String shopUrl="";
		try{
			
			Integer shopId=json.getInteger("rstShopId");
			prov=json.getString("prov");//省
			
			Element element = doc.ownerDocument().select("textarea.ks-datalazyload").first();
			Document doc1= Jsoup.parse(element.html().replace("&lt;", "<").replace("&gt;",">"));
//			if(null!=element){
				// <li class="shopkeeper">
//				 <div class="right">
//                 <a href="//rate.taobao.com/user-rate-UvFx0OFkWMFHuvgTT.htm" data-spm="d4918097">木林森户外旗舰店</a>
//             </div>
				Element shopkeeper=doc.ownerDocument().select("a.slogo-shopname").first();
				if(null!=shopkeeper){
					shopUrl="https:" + shopkeeper.attr("href");						
					shopName=StringUtil.replaceAll(shopkeeper.text());
					
				}

			   Element agen=doc1.ownerDocument().select("span.tm-shop-age-num").first();
			   if(null!=agen){
				   String a= StringUtil.replaceAll((agen.text()));
				   if(StringUtil.isNotEmptyStr(a)){
					   ageNum=Integer.parseInt(a);
				   }
			   }
			   Element locus=doc1.ownerDocument().select("li.locus").first();
			   if(null!=locus){
				   Element lo= locus.select("div.right").first();
				   if(lo!=null){
					   String location=StringUtil.replaceAll(lo.text());
					   if(location.indexOf(",")>-1){
							city=   StringUtil.replaceAll(location.split(",")[1]);
					   }else{
						   city=location;
					   }
				   }
			   }
		}catch(Exception ex){
			
		}
	}
	
	/**
	 * 获取商品价格信息
	 * @param itemInfo
	 * @return
	 */
	private Map<String,Object> getPromit(ItemInfo itemInfo){
		Map<String,Object> result=new HashMap<String,Object>();
		long beginTimex=	System.currentTimeMillis();
		try{	
			
//			itemInfo.setItemId(39993036168L);
//			itemInfo.setCatId(50026393L);
			
		//String url="https://mdskip.taobao.com/core/initItemDetail.htm?isPurchaseMallPage=false&showShopProm=false&tmallBuySupport=true&isApparel=false&addressLevel=2&isSecKill=false&offlineShop=false&itemId="+ itemInfo.getItemId() +"&tryBeforeBuy=false&isAreaSell=false&service3C=false&queryMemberRight=true&isForbidBuyItem=false&cachedTimestamp=1501816217002&isRegionLevel=false&household=false&cartEnable=true&sellerPreview=false&isUseInventoryCenter=false&callback=setMdskip&timestamp=1501826916595&isg=null&isg2=AiUlEL0PkKCcpfT4p5uWSSYjPOFfYtn0XMlSdScJNtx8PkOw67P5xQmUnJu1&areaId=310100&cat_id=" + itemInfo.getCatId();
		String url="https://mdskip.taobao.com/core/initItemDetail.htm?isPurchaseMallPage=false&showShopProm=false&tmallBuySupport=true&isApparel=false&addressLevel=2&isSecKill=false&offlineShop=false&itemId="+ itemInfo.getItemId() +"&tryBeforeBuy=false&isAreaSell=false&service3C=false&queryMemberRight=true&isForbidBuyItem=false&cachedTimestamp=1501816217002&isRegionLevel=false&household=false&cartEnable=true&sellerPreview=false&isUseInventoryCenter=false&callback=setMdskip&timestamp=1501826916595&isg=null&isg2=AiUlEL0PkKCcpfT4p5uWSSYjPOFfYtn0XMlSdScJNtx8PkOw67P5xQmUnJu1&areaId=310100";			
		String html =getItemDetailInfoFromUrl(url,itemInfo.getItemUrl());
		//String html= FileUtil.readTxtFile("d:\\cx.txt","UTF-8");
		//html="jsonp245(" + html + ");";
		if(StringUtil.isNotEmptyStr(html)){
			//System.out.println(html);
			//System.out.println(html.indexOf("({"));
			html=html.substring(html.indexOf("({")+1);
			html=html.substring(0,html.indexOf(")"));
			JSONObject json = JSON.parseObject(html);
			JSONObject defaultModel=json.getJSONObject("defaultModel");
			
			JSONObject sellCountDO= defaultModel.getJSONObject("sellCountDO");
			if(null!=sellCountDO){
				Long sellCount=sellCountDO.getLong("sellCount");
				result.put("sellCount", sellCount);
			}
			
			
			JSONObject itemPriceResultDO= defaultModel.getJSONObject("itemPriceResultDO");
	
			JSONArray tmallShopProms=itemPriceResultDO.getJSONArray("tmallShopProm");
			List<Map<String,Object>> shopProms=new ArrayList<Map<String,Object>>();
			if(null!=tmallShopProms && tmallShopProms.size()>0){
				
				for(int i=0;i<tmallShopProms.size();i++){
					Map<String,Object> shopProm=new HashMap<String,Object>();
					try{
						JSONObject prom=tmallShopProms.getJSONObject(i);
						Long campaignId=prom.getLong("campaignId");
						String campaignName=prom.getString("campaignName");
						Long startTime=prom.getLong("startTime");
						Long endTime=prom.getLong("endTime");
						
						shopProm.put("campaignId", campaignId);
						shopProm.put("campaignName", campaignName);
						shopProm.put("startTime", startTime);
						shopProm.put("endTime", endTime);
						
						
						String proMsg="";
						JSONArray promPlanMsgs=prom.getJSONArray("promPlanMsg");
						for(int j=0;j<promPlanMsgs.size();j++){
							if(StringUtil.isNotEmptyStr(proMsg)){
								proMsg=proMsg + "&" + promPlanMsgs.getString(j);
							}else{
								proMsg=promPlanMsgs.getString(j);
							}
						}
						shopProm.put("promPlanMsg", proMsg);
						shopProms.add(shopProm);
					}catch(Exception ex){
						
					}
					


				}
			}
			result.put("shopProms", shopProms);
			
			
			List<Map<String,Object>> skuPrices=new ArrayList<Map<String,Object>>();
			Map<String, Object> map = JSON.parseObject(
					itemPriceResultDO.getJSONObject("priceInfo").toJSONString(),new TypeReference<Map<String, Object>>(){} );
			Iterator iter = map.entrySet().iterator();
			while (iter.hasNext()) {
				Map<String,Object> skuPrice=new HashMap<String,Object>();
				try{
					Map.Entry entry = (Map.Entry) iter.next();
					String skuId = entry.getKey().toString();
					Object val = entry.getValue();
					JSONObject  skuProJson= JSON.parseObject(val.toString());
					String price=skuProJson.getString("price");
					
					skuPrice.put("skuId", skuId);
					skuPrice.put("price", price);
					
					JSONArray jarry=skuProJson.getJSONArray("promotionList");
					if(null==jarry){
						jarry=skuProJson.getJSONArray("suggestivePromotionList");
					}
					if(null!=jarry && jarry.size()>0){
						JSONObject pro=jarry.getJSONObject(0);
						Long startTime=pro.getLong("startTime");
						Long endTime=pro.getLong("endTime");
						String propPice=pro.getString("price");
						String priceType=pro.getString("type");
						
						
						skuPrice.put("startTime", startTime);
						skuPrice.put("endTime", endTime);
						skuPrice.put("propPice", propPice);
						skuPrice.put("priceType", priceType);
					}
					
					skuPrices.add(skuPrice);
				}catch(Exception ex){
					
				}
				

			}
			result.put("skuPrices", skuPrices);
		}
		}catch(Exception ex){
			
		}
		
		 long endTimex=	System.currentTimeMillis();
		System.out.println(String.format("Execute InitPromit !ok consume %s ms", endTimex - beginTimex));
		return result;
		
	}
	
	private void getRateTotal(ItemInfo itemInfo){
		long beginTime=System.currentTimeMillis();
		String url="https://dsr-rate.tmall.com/list_dsr_info.htm?itemId="+ itemInfo.getItemId() +"&spuId="+ itemInfo.getSpuId()+"&sellerId="+ itemInfo.getSellerId()+"&_ksTS=1502092746466_211&callback=jsonp212";;			
		String html =getItemDetailInfoFromUrl(url,itemInfo.getItemUrl());
		//jsonp212({"dsr":{"gradeAvg":4.8,"itemId":0,"peopleNum":0,"periodSoldQuantity":0,"rateTotal":1650,"sellerId":0,"spuId":0,"totalSoldQuantity":0}})
		html=html.substring(html.indexOf("({")+1);
		html=html.substring(0,html.indexOf(")"));
		JSONObject json = JSON.parseObject(html);
		JSONObject dsr=json.getJSONObject("dsr");
		Long rateTotal=dsr.getLong("rateTotal");
		
	    long endTime=	System.currentTimeMillis();
		System.out.println(String.format("Execute InitRateTotal !ok consume %s ms", endTime - beginTime));
		
		//System.out.println(html);
	}
	private static String getItemDetailInfoFromUrl(String url,String referer){
		String result="";
		try{
			Map<String, String> headers=new HashMap<String,String>();
			headers.put("Referer", referer);
			String urlEncoding="UTF-8";
			long beginTime=System.currentTimeMillis();
			result =HttpUtil.doGet(url,headers);
			if(StringUtil.isBlank(result)){
				result =HttpUtil.doGet(url,headers);
				if(StringUtil.isBlank(result)){
					result =HttpUtil.doGet(url,headers);
				}
			}
			long endTime=	System.currentTimeMillis();
			System.out.println(String.format("Execute getItemDetailInfoFromUrl !ok consume %s ms  url %s", endTime - beginTime,url));
		}catch(Exception ex){
			
		}
		return result;
	}
	
	private void getItemInfo(Document doc,JSONObject json){
		String itemName="";
		Double price=null;
		Long monthSale=0L;
		Long totalSale=0L;
		String categoryId="";
		Long collectCount=0L;
		Long status=0L;
		Long spuId=0L;
		Long sellerId=0L;
		
		try{
			JSONObject itemDO=json.getJSONObject("itemDO");
			JSONObject rateConfig=json.getJSONObject("rateConfig");
			sellerId=rateConfig.getLong("sellerId");
			categoryId=itemDO.getString("categoryId");					
			spuId=itemDO.getLong("spuId");	
			
			itemName=itemDO.getString("title");
			
			itemInfo.setSellerId(sellerId.toString());
			itemInfo.setCategoryId(categoryId);
			itemInfo.setSpuId(spuId);
			itemInfo.setItemName(itemName);
			
		}catch(Exception ex){
			
		}			
	}
	private JSONObject InitTshopJson(String html){
		String tshop=html.substring(html.indexOf("TShop.Setup(") +12);
		tshop=tshop.substring(0,tshop.indexOf(");"));
			
		JSONObject json = JSON.parseObject(tshop);
		return json;
	}
	

}

//package com.ssm.demo.lucene;
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.lucene.analysis.Analyzer;
//import org.apache.lucene.analysis.standard.StandardAnalyzer;
//import org.apache.lucene.document.Document;
//import org.apache.lucene.document.Field;
//import org.apache.lucene.document.Field.Index;
//import org.apache.lucene.document.Field.Store;
//import org.apache.lucene.index.CorruptIndexException;
//import org.apache.lucene.index.IndexWriter;
//import org.apache.lucene.index.IndexWriter.MaxFieldLength;
//import org.apache.lucene.index.Term;
//import org.apache.lucene.queryParser.QueryParser;
//import org.apache.lucene.search.IndexSearcher;
//import org.apache.lucene.search.Query;
//import org.apache.lucene.search.ScoreDoc;
//import org.apache.lucene.search.Sort;
//import org.apache.lucene.search.SortField;
//import org.apache.lucene.search.TopDocs;
//import org.apache.lucene.store.Directory;
//import org.apache.lucene.store.FSDirectory;
//import org.apache.lucene.util.Version;
//import org.wltea.analyzer.lucene.IKQueryParser;
//
//import com.ssm.demo.entity.User;
///**
// *  此案例实现Lucene向索引库中添加索引和查询索引的功能   测试使用
// * @author Administrator
// *
// */
//public class HelloWordLucene {
//
//	/**
//	 * 把good商品对象添加到索引库中
//	 * @param goods
//	 */
//	public void addDocument(User goods){
//		//创建indexWriter
//		IndexWriter indexwriter=null;
//		try {
//			indexwriter=LuceneUtil.getIndexWriter();
//			//把goods对象转为document
//			//Document doc=new Document();
//			/**
//			 * Store配置field字段是否存储到索引库
//			 * YES：字段存储到索引库中，以后查询的时候可以查询出来
//			 * No：不存储到索引库中
//			 *  Index: Lucene为提高查询效率,会像字典一样创建索引. 配置此字段是否要建立索引(建立索引的Field就是Term),
//			 *  如果建立索引以后就可以通过此字段查询记录
//			 *   NOT_ANALYZED: 创建索引,但是Field的不分词(不分开) 整体作为一个索引
//			 *   ANALYZED: 不但要建立索引此Field会被分词(可能一个Field分为多个Term的情况)
//			 *   NO: 不建立索引,以后不能通过此字段查询数据 
//			 *  Store yes Index: ANALYZED: 此Field可以存储,而且Field 关键字支持分词
//			 *  Store yes Index： NOT_ANALYZED 此Field可以存储,但是Field不支持分词,作为一个完成Term   例如: 数字 id  price  和URL 专业词汇
//			 *  Store yes Index: NO:  可以查询出此字段, 但是此字段不作为查询关键字
//			 *  Store no  Index: ANALYZED:  此Field不存储,但是此Field可以做为关键字搜索  
//			 *  Store no  Index: NOT_ANALYZED: 此Field不存储,但是此Field可以做为整体(不拆分)关键字搜索
//			 *  Store no  Index: NO:  既不建索引也不存储 没有任何意义,如果这样配置则会抛出异常
//			 */
////			doc.add(new Field("id", goods.getId().toString(), Store.YES, Index.NOT_ANALYZED));
////			doc.add(new Field("name", goods.getName(), Store.YES, Index.ANALYZED));
////			doc.add(new Field("price", goods.getPrice().toString(), Store.YES, Index.NOT_ANALYZED));
////			doc.add(new Field("remark", goods.getRemark(), Store.NO, Index.ANALYZED));
//			indexwriter.addDocument(DocumentUtil.goodsToDocument(goods));
//			// 如果没有提交,在没有异常的情况close()之前会自动提交
//			indexwriter.commit();
//		} catch (Exception e) {
//			try {
//				indexwriter.rollback();
//				throw new RuntimeException(e);
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				throw new RuntimeException(e);
//			}
//		}
//	}
//	/**
//	 * 根据指定的条件查询，
//	 * @param name 指定的关键字
//	 * @return 封装了goods对象的list集合
//	 */
//	public List<User> queryGoods(String keyword){
//		List<User> goodsList=new ArrayList<User>();
//		//创建查询对象
//		IndexSearcher searcher=null;
//		try {
//			searcher=LuceneUtil.getIndexSearcher();
//			//  指定查询的关键字到索引库查询
//			//Query query=IKQueryParser.parse("userName", name);
//			Query query=IKQueryParser.parseMultiField(new String[]{"userName","password"}, keyword);
//			/**
//			 * 根据给定的关键字查询,与索引库Term去匹配，5代表: 期望返回的结果数
//			 *  第一次查询: indexSearcher.search 只能获取文档的索引号和匹配的数量
//			 *  返回的结果是TopDoc类型
//			 *  totalHits: 命中数, 数组的长度，后面用来做分页
//			 *  ScoreDoc[]: 存储匹配的文档编号的数组
//			 *  Score: 文档的积分,按照命中率自动算出来
//			 *  Doc：当前文档的编号
//			 */
//			//TopDocs topDocs= searcher.search(query, 5);
//			Sort sort=new Sort(new SortField("id",SortField.INT, true),new SortField("age", SortField.INT,true));  
//            TopDocs topDocs= searcher.search(query, null, 5, sort);  
//			// 此变量/每页显示的记录数就是总页数
//			System.out.println("真正命中的结果数:" + topDocs.totalHits);
//			// 返回的是符合条件的文档编号,并不是文档本事
//			ScoreDoc scoreDocs[]= topDocs.scoreDocs;
//			for(int i=0;i<scoreDocs.length;i++){
//				ScoreDoc scoreDoc= scoreDocs[i];
//				System.out.println("真正的命中率："+scoreDoc.score);
//				System.out.println("存储的是文档编号："+scoreDoc.doc);
//				Document doc= searcher.doc(scoreDoc.doc);
//				System.out.println(doc.get("id"));
//				System.out.println(doc.get("userName"));
//				System.out.println(doc.get("password"));
//				System.out.println(doc.get("age"));
//				System.out.println("---------");
////				Goods goods=new Goods();
////				goods.setId(Integer.parseInt(doc.get("id")));
////				goods.setName(doc.get("name"));
////				goods.setPrice(Double.parseDouble(doc.get("price")));
////				goods.setRemark(doc.get("remark"));
//				doc.getField("userNmae").setValue(HighlighterUtil.setHighlighterText(query, doc.get("userName"), 10));  
//                doc.getField("password").setValue(HighlighterUtil.setHighlighterText(query, doc.get("password"), 15));
//				goodsList.add(DocumentUtil.documentToGoods(doc));
//			}
//			
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			throw new RuntimeException(e);
//		} 
//		return goodsList;
//	}
//	
//	public void deleteDocument(int id){
//		//创建indexWriter
//		IndexWriter indexwriter=null;
//		try {
//			indexwriter=LuceneUtil.getIndexWriter();
//			// 一般来说都是通过id来删除,所以即使是通过name查询,ID也要建索引,因为更新和删除需要id
//			// 根据ID把符合条件的document对象删除掉,但是索引(term) 并没有删除
//			indexwriter.deleteDocuments(new Term("id", id+""));
//			//同步删除索引库中的索引部分
//			indexwriter.optimize();
//			// 如果没有提交,在没有异常的情况close()之前会自动提交
//			indexwriter.commit();
//		} catch (Exception e) {
//			try {
//				indexwriter.rollback();
//				throw new RuntimeException(e);
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				throw new RuntimeException(e);
//			}
//		}
//	}
//	public void updateDocument(User goods){
//		IndexWriter indexwriter=null;
//		try{
//			indexwriter=LuceneUtil.getIndexWriter();
//			indexwriter.updateDocument(new Term("id", goods.getId().toString()), DocumentUtil.goodsToDocument(goods));
//			indexwriter.optimize();
//			indexwriter.commit();
//		}catch (Exception e) {
//			// TODO: handle exception
//			try {
//				indexwriter.rollback();
//				throw new RuntimeException(e);
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				throw new RuntimeException(e);
//			}
//			
//		}
//	}
//	/**
//	 * 实现分页的功能  
//	 * @param name 查询的关键字
//	 * @param currentPage 当前的页数
//	 * @return 记录数
//	 */
//	public List<User> queryByPage(String name,int currentPage){
//		int number=5; // 每页显示5条
//		List<User> goodsList=new ArrayList<User>();
//		IndexSearcher indexSearcher=null;
//		try {
//			// 创建查询对象
//			indexSearcher=LuceneUtil.getIndexSearcher();
//			// 指定查询的关键字
//			//Query query=IKQueryParser.parse("userName", name);
//			Query query=IKQueryParser.parseMultiField(new String[]{"userName","password"}, name);
//			TopDocs topDocs=indexSearcher.search(query,currentPage*number);
//			// 此变量/每页显示的记录数就是总页数
//			System.out.println("真正命中的结果数:" + topDocs.totalHits);
//			int totalPage=0;
//			if(topDocs.totalHits%number!=0){
//				totalPage=topDocs.totalHits/number+1;
//			}else{
//				totalPage=topDocs.totalHits/number;
//			}
//			System.out.println("通过系统的总结果数/每页显示的数量=总页数" + totalPage);
//			// 返回的是符合条件的文档编号,并不是文档本事
//			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
//			// 去期望值  和实际值的 最小值
//			System.out.println("真正查询出来的数组的长度:" + scoreDocs.length);
//			for(int i=(currentPage-1)*number;i<scoreDocs.length;i++){
//				ScoreDoc scoreDoc=scoreDocs[i];
//				System.out.println("存储了命中率积分:" + scoreDoc.score);
//				System.out.println("存储的是文档编号:" + scoreDoc.doc);
//				// 第二次查询: 通过文档的编号,查询真正的文档信息
//				Document document=indexSearcher.doc(scoreDoc.doc);
//				goodsList.add(DocumentUtil.documentToGoods(document));
//			}
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//		return goodsList;
//	}
//	
//}

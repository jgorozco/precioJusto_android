package com.jvk.preciojusto.frwk.dataModel.ImageUploadModels;

public class ImgUrl {
	public ImgUrlUpload upload;

	
	 public ImgUrl() {
			
		}
	
		
		
		public class ImgUrlUpload {
			public ImgUrlImage image;
			public ImgUrlLinks links;
		}
		 
	 
	 
	public class ImgUrlImage {
		public boolean name;
		public String title;
		public String caption;
		public String hash;
		public String deletehash;
		public String datetime;
		public String type;
		public String animated;
		public int width;
		public int height;
		public int size;
		public int views;
		public int bandwidth;
		
		 public ImgUrlImage() {
				
			}
	}
	public class ImgUrlLinks {
		public String original;
		public String imgur_page;
		public String delete_page;
		public String small_square;
		public String large_thumbnail;
		
		
		 public ImgUrlLinks() {
				
			}
	}

	

	
	
	


}

package com.tui.image.converter.controller;

import java.awt.Rectangle;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import magick.DrawInfo;
import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * Handles requests for the application file upload requests
 */
@Controller
public class FileUploadController {

	private static final Logger logger = LoggerFactory
			.getLogger(FileUploadController.class);

	/**
	 * Upload single file using Spring Controller
	 */
	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	public @ResponseBody
	String uploadFileHandler(@RequestParam("name") String name,
			@RequestParam("file") MultipartFile file) {

		if (!file.isEmpty()) {
			try {
				byte[] bytes = file.getBytes();

				// Creating the directory to store file

				File dir = new File("M:\\uploadedImage");
				if (!dir.exists())
					dir.mkdirs();

				// Create the file on server
				File serverFile = new File(dir.getAbsolutePath()
						+ File.separator + name);
				BufferedOutputStream stream = new BufferedOutputStream(
						new FileOutputStream(serverFile));
				stream.write(bytes);
				stream.close();
				System.setProperty("jmagick.systemclassloader", "no");
				cropUploadedImageXS(serverFile);
				resizeUploadedImageSM(serverFile);
				logger.info("Server File Location="
						+ serverFile.getAbsolutePath());

				return "You successfully uploaded file=" + name;
			} catch (Exception e) {
				return "You failed to upload " + name + " => " + e.getMessage();
			}
		} else {
			return "You failed to upload " + name
					+ " because the file was empty.";
		}
	}

	private void resizeUploadedImageSM(File serverFile) throws IOException {

		String imageName = serverFile.getAbsolutePath();
		String[] splitedName = imageName.split("\\.");
		StringBuilder cmdBuilder = new StringBuilder();
		String[] cmdResize = new String[]{"C:\\ImageMagick-6.9.1-Q16\\convert.exe",imageName,"-resize","500x500^",splitedName[0]+"_sm."+splitedName[1]};
		/*cmdBuilder.append("convert ").append(imageName).append(" ")
				.append("-resize ").append("500x500^ ").append(splitedName[0]).append("_sm.").append(splitedName[1]);
		String cmdResize = cmdBuilder.toString();*/

		Runtime.getRuntime().exec(cmdResize);

		System.out.println("Image resized");
		/*
		 * ImageInfo imageInfo = new ImageInfo(serverFile.getAbsolutePath());
		 * MagickImage magicImage=new MagickImage(imageInfo); DrawInfo drawInfo
		 * = new DrawInfo(imageInfo); drawInfo.setGeometry("500+500");
		 * 
		 * magicImage.annotateImage(drawInfo);
		 * System.out.println("Image File Name is: "+serverFile.getName());
		 * magicImage.setFileName(serverFile.getName()+"_SM"); Rectangle
		 * rectangle = new Rectangle(10,10,500,500);
		 * magicImage.cropImage(rectangle);
		 */

	}

	private void cropUploadedImageXS(File serverFile) throws IOException {

		String imageName = serverFile.getAbsolutePath();
		String[] splitedName = imageName.split("\\.");
		StringBuilder cmdBuilder = new StringBuilder();
		/*cmdBuilder.append("convert ").append(imageName).append(" ")
				.append("-crop ").append("100x100^ ")
				.append("-gravity center ").append(splitedName[0]).append("_xs.")
				.append(splitedName[1]);*/
		String[] cmdCrop = new String[]{"C:\\ImageMagick-6.9.1-Q16\\convert.exe",imageName,"-gravity", "center","-crop","100x100+10+10",splitedName[0]+"_xs."+splitedName[1]};

		Runtime.getRuntime().exec(cmdCrop);

		System.out.println("Image cropped");
		/*
		 * ImageInfo imageInfo = new ImageInfo(serverFile.getAbsolutePath());
		 * MagickImage magicImage=new MagickImage(imageInfo); DrawInfo drawInfo
		 * = new DrawInfo(imageInfo); drawInfo.setGeometry("100+100");
		 * 
		 * magicImage.annotateImage(drawInfo);
		 * System.out.println("Image File Name is: "+serverFile.getName());
		 * String extension = serverFile.getName();
		 * magicImage.setFileName(serverFile.getName()+"_XS"); Rectangle
		 * rectangle = new Rectangle(10,10,100,100);
		 * magicImage.cropImage(rectangle);
		 */
	}

	/**
	 * Upload multiple file using Spring Controller
	 */
	@RequestMapping(value = "/uploadMultipleFile", method = RequestMethod.POST)
	public @ResponseBody
	String uploadMultipleFileHandler(@RequestParam("name") String[] names,
			@RequestParam("file") MultipartFile[] files) {

		if (files.length != names.length)
			return "Mandatory information missing";

		String message = "";
		for (int i = 0; i < files.length; i++) {
			MultipartFile file = files[i];
			String name = names[i];
			try {
				byte[] bytes = file.getBytes();

				// Creating the directory to store file
				String rootPath = System.getProperty("catalina.home");
				File dir = new File(rootPath + File.separator + "tmpFiles");
				if (!dir.exists())
					dir.mkdirs();

				// Create the file on server
				File serverFile = new File(dir.getAbsolutePath()
						+ File.separator + name);
				BufferedOutputStream stream = new BufferedOutputStream(
						new FileOutputStream(serverFile));
				stream.write(bytes);
				stream.close();

				logger.info("Server File Location="
						+ serverFile.getAbsolutePath());

				message = message + "You successfully uploaded file=" + name
						+ "<br />";
			} catch (Exception e) {
				return "You failed to upload " + name + " => " + e.getMessage();
			}
		}
		return message;
	}

	@RequestMapping("/")
	public String renderFileUploadView() {
		return "fileUpload";
	}
}
package com.example.grpc.client.grpcclient;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.ui.Model;

@Controller
public class PingPongEndpoint {    

	int matrixA[][];
	int matrixB[][];

	// the clients main operations
	// found in GrpcClientService.java
	GRPCClientService grpcClientService; 

	@Autowired
	public PingPongEndpoint(GRPCClientService grpcClientService) {
		this.grpcClientService = grpcClientService;
	}    

	// ping mapping to verify the server is up
	@GetMapping("/ping")
    public String ping() {
        return grpcClientService.ping();
    }

	// add mapping (for adding matrices)
    @RequestMapping("/add")
	public String add(Model model) {
		int[][] resp = grpcClientService.add(matrixA,matrixB);
		model.addAttribute("A",matrixA);
		model.addAttribute("B",matrixB);
		model.addAttribute("result",resp);
		return "upload";
	}

	// multiply mapping (for multiplying matrices)
	@RequestMapping("/multiply")
	public String multiply(Model model) {
		int[][] resp = grpcClientService.multiply(matrixA,matrixB,10);
		model.addAttribute("A",matrixA);
		model.addAttribute("B",matrixB);
		model.addAttribute("result",resp);
		return "upload";
	}

	@RequestMapping("/")
    public String main(){
		return "interface";
    }

	public int[][] addToMatrix(Model model, String matrix_string) {
		int returner[][];

		String[] rows= matrix_string.split("\n");
		int rowLen = rows.length;
		String columns[] = rows[0].split(" ");
		int columnLen = columns.length;
		returner = new int[rowLen][columnLen];

		for (int i=0; i<rowLen; i++) {
			columns = rows[i].split(" ");
			if (columns.length != rowLen){
				model.addAttribute("status","Please upload a square matrix!");
			}
			for(int j=0; j<columnLen; j++){
			try{
				int numberA =  Integer.parseInt(columns[j].replaceAll("[^0-9]", ""));
				returner[i][j] = numberA;
			}catch(NumberFormatException ex){
				System.out.println("Has to be numeric");
			}
			}
		}

		return returner;
	}

    @RequestMapping("/upload")
    public String uploadFiles(Model model, @RequestParam("file") MultipartFile[] files) {
		String matrixA_string = "";
		String matrixB_string = "";

		if (files.length!=2){
			model.addAttribute("status","Only 2 files at a time");
			return "interface";
		}

		try{
			byte[] file1 = files[0].getBytes();
			byte[] file2 = files[1].getBytes();

			matrixA_string = new String(file1);
			matrixB_string = new String(file2);
		}catch(IOException e){
			model.addAttribute("status","Error reading files!");
			return "interface";
		}

		matrixA = addToMatrix(model, matrixA_string);
		matrixB = addToMatrix(model, matrixB_string);

		if(matrixA.length != matrixB.length){
			model.addAttribute("status","Both matrices should be of the same size");
			return "interface";
		}

		model.addAttribute("message","Successfully uploaded!");
		model.addAttribute("A",matrixA);
		model.addAttribute("B",matrixB);
		return "upload";

    }
}


package com.hatcherdev.AdvGraphPersonalization.DataLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartLoader {

    public static void main(String[] args){

        Logger logger = LoggerFactory.getLogger(StartLoader.class);

        if (args.length == 0){
            logger.error("You must specify an argument of the file path to process");
        }

        String filePath = args[0];

        DataLoader dataLoader = new DataLoader(filePath);
        dataLoader.run();

        System.exit(0);

    }

}

package fileDownloading;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author User 1 on 9/13/2019
 * @project filedownloading
 */
@RestController
public class Controller {

    private static Logger logger = Logger.getLogger(Controller.class);

    @Autowired
    private FileDetectorService fileDetectorService;

    @RequestMapping("/")
    public String getHome() {
        logger.debug("Home page successful.");
        return "<h1 style='text-align:center;margin-top: 20%;'>File downloader Bot is online</h1>";
    }

    @RequestMapping("/{name}")
    public String downloadByName(@PathVariable("name") String name, HttpServletResponse response) throws IOException {
        logger.info("Download started.");
        List<String> fileNames = fileDetectorService.getAllFileNames(name);
        if (fileNames != null) {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + name + "Videos.zip");
            response.setStatus(HttpServletResponse.SC_OK);

            try (ZipOutputStream zippedOut = new ZipOutputStream(response.getOutputStream())) {
                logger.info(fileNames.size() + " files found.");
                logger.info("Creating a zip file out of " + fileNames.size() + " files.");
                for (String file : fileNames) {
                    FileSystemResource resource = new FileSystemResource(file);
                    ZipEntry e = new ZipEntry(Objects.requireNonNull(resource.getFilename()));
                    // Configure the zip entry, the properties of the file
                    e.setSize(resource.contentLength());
                    e.setTime(System.currentTimeMillis());
                    zippedOut.putNextEntry(e);
                    // And the content of the resource:
                    StreamUtils.copy(resource.getInputStream(), zippedOut);
                    zippedOut.closeEntry();
                }
                zippedOut.finish();
                logger.info("Download finalized!");
                return "File downloader Bot is downloading " + name;
            }
        } else {
            return "<h1 style='text-align:center;margin-top: 20%;'>File downloader Bot does not found given name " + name + "</h1>";
        }
    }
}

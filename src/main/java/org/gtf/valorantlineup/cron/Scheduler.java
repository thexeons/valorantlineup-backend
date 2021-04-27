package org.gtf.valorantlineup.cron;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.time.DateUtils;
import org.gtf.valorantlineup.models.Image;

import org.gtf.valorantlineup.models.Node;
import org.gtf.valorantlineup.repositories.ImageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;

import java.time.OffsetDateTime;
import java.util.List;

@Component
public class Scheduler {

    public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

//    Spring Cron :
//    second,
//    minute,
//    hour,
//    day of month,
//    month,
//    day(s) of week.

    @Autowired
    ImageRepository imageRepository;

    @Value("${upload.path}")
    String uploadPath;

//    @Autowired
//    EntityManager em;


    @Scheduled(cron = "20 53 * * * *")
    public void cronJobScheduler() {

//        Query q = em.createNativeQuery("select * from images where node_id is null;", Image.class);
//        List<Image> result = q.getResultList();

        List<Image> result = imageRepository.findAllByNodeIsNullAndCreatedDateBefore(OffsetDateTime.now().minusDays(1));
        for (Image a : result) {
                File fileToDelete = FileUtils.getFile(uploadPath+a.getSavedName());
                boolean success = FileUtils.deleteQuietly(fileToDelete);
                if(success) {
                    imageRepository.delete(a);
                }
                else {
                    LOGGER.info("Deletion of " + a.getSavedName() + "failed");
                }
        }
    }
}
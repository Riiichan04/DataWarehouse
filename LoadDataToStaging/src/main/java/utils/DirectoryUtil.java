package utils;

import java.io.File;
import java.time.Clock;
import java.time.LocalDate;

public interface DirectoryUtil {
    /**
     * Get all files in directory
     * @param directoryPath Path to directory
     * @return List file in directory or return file as a list if directory is a file
     */
    static File[] scanDirectory(String directoryPath) {
        File dir = new File(directoryPath);
        if (!dir.exists()) return null;
        if (dir.isDirectory()) {
            return new File[]{dir};
        } else return dir.listFiles();
    }

    /**
     * Get result file for a date
     * @param directoryPath Path of directory contain crawl result
     * @param offsetLocalDate Offset time from today
     * @return List result file of today
     */
    static File[] getAllFileByDate(String directoryPath, OffsetLocalDate offsetLocalDate) {
        File dir = new File(directoryPath);
        if (!dir.exists() || !dir.isDirectory()) return null;
        return dir.listFiles(file -> {
            LocalDate localDate = getUTCLocalDate(offsetLocalDate);
            String year = localDate.getYear() + "";
            String month = localDate.getMonthValue() + "";
            String day = localDate.getDayOfMonth() + "";

            if (month.length() == 1) month = "0" + month;
            if (day.length() == 1) day = "0" + day;

            String regex = "^result_" + day + "_" + month + "_" + year + "$";

            String fileName = file.getName();
            String name = fileName.substring(0, fileName.lastIndexOf("."));
            String extension = fileName.substring(fileName.lastIndexOf("."));
            return extension.equals(".csv") && regex.matches(name);
        });
    }

    /**
     * Get UTC for input date
     * @param offsetLocalDate Offset for LocalDate result
     * @return LocalDate after modified by offsetLocalDate
     */
    static LocalDate getUTCLocalDate(OffsetLocalDate offsetLocalDate) {
        try {
            if (offsetLocalDate == null) {
                return getTodayDate();
            } else return getTodayDate()
                    .plusDays(offsetLocalDate.getDays())
                    .plusWeeks(offsetLocalDate.getWeeks())
                    .plusMonths(offsetLocalDate.getMonths())
                    .plusYears(offsetLocalDate.getYears());
        } catch (Exception e) {
            return getTodayDate();
        }
    }

    /**
     * Get UTC for today
     * @return LocalDate object for today
     */
    static LocalDate getTodayDate() {
        Clock utcClock = Clock.systemUTC();
        return LocalDate.now(utcClock);
    }

}

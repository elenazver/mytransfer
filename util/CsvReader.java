package org.example.util;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.example.dto.youtrack.UserYouTrackDto;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class CsvReader {
    public static List<UserYouTrackDto> readUsersFromCsv(String csvFilename) {
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader(); // We take into account the headlines

        try (InputStream inputStream = CsvReader.class.getClassLoader().getResourceAsStream(String.format("%s.csv", csvFilename))) {
            if (inputStream == null) {
                throw new IOException("CSV the file was not found in resources");
            }

            MappingIterator<UserYouTrackDto> it = csvMapper.readerFor(UserYouTrackDto.class)
                    .with(schema)
                    .readValues(inputStream);

            return it.readAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

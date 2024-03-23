package asist.io.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import asist.io.exception.ModelException;

@Component
public class ExcelGenerator {
    private static final  Logger logger =  Logger.getLogger(ExcelGenerator.class);

    /**
     * Escribe una tabla en un archivo de Excel y lo devuelve en un ResponseEntity
     * @param tabla Tabla a escribir
     * @param nombreArchivo Nombre del archivo
     * @return ResponseEntity con el archivo de Excel
     */
    @SuppressWarnings("null")
    public static ResponseEntity<ByteArrayResource> escribirTablaEnExcel(List<List<Object>> tabla, String nombreArchivo) {
        try{        
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Asistencias");
            
            logger.info ("Creando archivo de excel con nombre: " + nombreArchivo);

            CellStyle checkStyle = crearEstiloCheck(workbook);
            CellStyle crossStyle = crearEstiloCruz(workbook);
            CellStyle cellStyle = crearEstiloCelda(workbook);


            crearHeader(sheet, cellStyle, nombreArchivo);
            

            int rowNum = 1;
            for (List<Object> rowData : tabla) {
                crearFila(sheet, rowData, rowNum++, cellStyle, checkStyle, crossStyle);
            }

            ajustarAnchoColumnas(sheet, tabla.get(0).size());

            ByteArrayOutputStream outputStream = woorkBookAByteArrayOutputStream(workbook);

            ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreArchivo + ".xlsx\"");

            return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);

        }catch (Exception e){
            logger.error("Error al crear archivo de excel" + e.getMessage());
            throw new ModelException ("Error al crear archivo de excel");
        }
        
    }


    /**
     * Header generator, crea un header para el archivo de excel
     * @param sheet
     * @param rowData
     * @param rowNum
     * @param cellStyle
     * @param checkStyle
     * @param crossStyle
     */
    private static void crearHeader(Sheet sheet, CellStyle cellStyle, String nombreArchivo){
        String[] nombreArchivoParts = nombreArchivo.split(" - ");
        String curso = nombreArchivoParts[0];
        String periodo = nombreArchivoParts.length > 1 ? nombreArchivoParts[1] : "";

        Row headerRow = sheet.createRow(0);
        String[] encabezados = {"Curso:", curso, "Periodo:", periodo};
        for (int i = 0; i < encabezados.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(encabezados[i]);
            cell.setCellStyle(cellStyle);
        }
    }

    /**
     * Escribe una fila en un archivo de Excel
     * @param sheet Hoja de cálculo
     * @param rowData Datos de la fila
     * @param rowNum Número de la fila
     * @param cellStyle Estilo de las celdas
     * @param checkStyle Estilo de las celdas con "✔"
     * @param crossStyle Estilo de las celdas con "❌"
     */
    private static void crearFila(Sheet sheet, List<Object> rowData, int rowNum, CellStyle cellStyle, CellStyle checkStyle, CellStyle crossStyle) {
        Row row = sheet.createRow(rowNum);
        int colNum = 0;
        for (Object field : rowData) {
            crearCelda(row, field, colNum++, cellStyle, checkStyle, crossStyle);
        }
    }

    /**
     * Escribe una celda en un archivo de Excel
     * @param row Fila
     * @param field Dato de la celda
     * @param colNum Número de la columna
     * @param cellStyle Estilo de las celdas
     * @param checkStyle Estilo de las celdas con "✔"
     * @param crossStyle Estilo de las celdas con "❌"
     */
    private static void crearCelda(Row row, Object field, int colNum, CellStyle cellStyle, CellStyle checkStyle, CellStyle crossStyle) {
        Cell cell = row.createCell(colNum);
        if (field instanceof String) {
            cell.setCellValue((String) field);
            cell.setCellStyle(cellStyle); // Aplica el estilo a las celdas con texto
        } else if (field instanceof Boolean) {
            if ((Boolean) field) {
                cell.setCellValue("✔");
                cell.setCellStyle(checkStyle); // Aplica el estilo a las celdas con "✔"
            } else {
                cell.setCellValue("❌");
                cell.setCellStyle(crossStyle); // Aplica el estilo a las celdas con "❌"
            }
        }
    }

    /**
     * Ajusta el ancho de las columnas de una hoja de cálculo
     * @param sheet Hoja de cálculo
     * @param columnCount Número de columnas
     */
    private static void ajustarAnchoColumnas(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * Escribe un libro de trabajo en un ByteArrayOutputStream
     * @param workbook Libro de trabajo
     * @return ByteArrayOutputStream con el libro de trabajo
     */
    private static ByteArrayOutputStream woorkBookAByteArrayOutputStream(Workbook workbook) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try  {
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close(); // Asegúrate de cerrar el ByteArrayOutputStream
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return outputStream;
    }

    /**
     * Crea un estilo de celda con un borde y una fuente
     * @param workbook Libro de trabajo
     * @return Estilo de celda
     */
    private static CellStyle crearEstiloCheck(Workbook workbook) {
        return createBorderedStyleWithFont(workbook, IndexedColors.GREEN, (short) 14, true);
    }

    /**
     * Crea un estilo de celda con un borde y una fuente
     * @param workbook Libro de trabajo
     * @return Estilo de celda
     */
    private static CellStyle crearEstiloCruz(Workbook workbook) {
        return createBorderedStyleWithFont(workbook, IndexedColors.RED, (short) 14, false);
    }
    
    /**
     * Crea un estilo de celda con un borde y una fuente
     * @param workbook Libro de trabajo
     * @return Estilo de celda
     */
    private static CellStyle crearEstiloCelda(Workbook workbook) {
        return createBorderedStyleWithFont(workbook, IndexedColors.BLACK, (short) 12, true);
    }

    /**
     * Crea un estilo de celda con un borde y una fuente
     * @param workbook Libro de trabajo
     * @param color Color de la fuente
     * @param fontSize Tamaño de la fuente
     * @param isBold Si la fuente es negrita
     * @return Estilo de celda
     */
    private static CellStyle createBorderedStyleWithFont(Workbook workbook, IndexedColors color, short fontSize, boolean isBold) {
        Font font = workbook.createFont();
        font.setColor(color.getIndex());
        font.setFontHeightInPoints(fontSize);
        font.setBold(isBold);

        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        return style;
    }
}
package com.glicemia.excel;

import com.glicemia.entity.*;
import com.glicemia.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExcelService {

    private final RefeicaoRepository refeicaoRepository;
    private final Medicao2HRepository medicao2HRepository;
    private final Pendencia2HRepository pendencia2HRepository;
    private final LogAlteracaoRepository logAlteracaoRepository;

    @Value("${app.excel.path}")
    private String excelPath;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final Object lock = new Object();

    // ==================== ESCRITA ====================

    public synchronized void exportarTodosParaExcel() {
        synchronized (lock) {
            try {
                File file = new File(excelPath);
                Workbook wb = file.exists()
                        ? WorkbookFactory.create(new FileInputStream(file))
                        : new XSSFWorkbook();

                exportarRefeicoes(wb);
                exportarMedicoes(wb);
                exportarPendencias(wb);
                exportarLogs(wb);

                try (FileOutputStream fos = new FileOutputStream(file)) {
                    wb.write(fos);
                }
                wb.close();
                log.info("📊 Excel exportado com sucesso: {}", excelPath);
            } catch (Exception e) {
                log.error("❌ Erro ao exportar Excel: {}", e.getMessage());
            }
        }
    }

    private void exportarRefeicoes(Workbook wb) {
        Sheet sheet = getOrCreateSheet(wb, "Refeições");
        sheet.createRow(0);
        criarCabecalho(sheet.getRow(0), new String[]{
            "ID", "Usuário", "Tipo", "MedAntes_DataHora", "ValorAntes",
            "Inicio_Ref", "Fim_Ref", "Observação"
        });

        List<Refeicao> lista = refeicaoRepository.findAllOrderByHorarioInicioDesc();
        for (int i = 0; i < lista.size(); i++) {
            Refeicao r = lista.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(r.getId());
            row.createCell(1).setCellValue(r.getUsuario().getUsername());
            row.createCell(2).setCellValue(r.getTipoRefeicao().name());
            row.createCell(3).setCellValue(fmt(r.getMedicaoAntesHorario()));
            row.createCell(4).setCellValue(r.getValorAntes());
            row.createCell(5).setCellValue(fmt(r.getHorarioInicio()));
            row.createCell(6).setCellValue(fmt(r.getHorarioFim()));
            row.createCell(7).setCellValue(r.getObservacao() != null ? r.getObservacao() : "");
        }
        autoSizeColumns(sheet, 8);
    }

    private void exportarMedicoes(Workbook wb) {
        Sheet sheet = getOrCreateSheet(wb, "Medições");
        sheet.createRow(0);
        criarCabecalho(sheet.getRow(0), new String[]{
            "ID", "RefeiçãoID", "Horario_Medicao", "Valor_Glicemia", "Observação"
        });

        List<Medicao2H> lista = medicao2HRepository.findAll();
        for (int i = 0; i < lista.size(); i++) {
            Medicao2H m = lista.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(m.getId());
            row.createCell(1).setCellValue(m.getRefeicao().getId());
            row.createCell(2).setCellValue(fmt(m.getHorarioMedicao()));
            row.createCell(3).setCellValue(m.getValorGlicemia());
            row.createCell(4).setCellValue(m.getObservacao() != null ? m.getObservacao() : "");
        }
        autoSizeColumns(sheet, 5);
    }

    private void exportarPendencias(Workbook wb) {
        Sheet sheet = getOrCreateSheet(wb, "Pendências 2H");
        sheet.createRow(0);
        criarCabecalho(sheet.getRow(0), new String[]{
            "ID", "RefeiçãoID", "Horario_Previsto", "Status", "Medicao2H_ID"
        });

        List<Pendencia2H> lista = pendencia2HRepository.findAll();
        for (int i = 0; i < lista.size(); i++) {
            Pendencia2H p = lista.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(p.getId());
            row.createCell(1).setCellValue(p.getRefeicao().getId());
            row.createCell(2).setCellValue(fmt(p.getHorarioPrevisto()));
            row.createCell(3).setCellValue(p.getStatus().name());
            row.createCell(4).setCellValue(p.getMedicao() != null ? p.getMedicao().getId() : 0);
        }
        autoSizeColumns(sheet, 5);
    }

    private void exportarLogs(Workbook wb) {
        Sheet sheet = getOrCreateSheet(wb, "Logs");
        sheet.createRow(0);
        criarCabecalho(sheet.getRow(0), new String[]{
            "ID", "Timestamp", "Usuário", "Ação", "Detalhes"
        });

        List<LogAlteracao> lista = logAlteracaoRepository.findTop50ByOrderByTimestampDesc();
        for (int i = 0; i < lista.size(); i++) {
            LogAlteracao l = lista.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(l.getId());
            row.createCell(1).setCellValue(fmt(l.getTimestamp()));
            row.createCell(2).setCellValue(l.getUsuario() != null ? l.getUsuario().getUsername() : "SISTEMA");
            row.createCell(3).setCellValue(l.getAcao() != null ? l.getAcao() : "");
            row.createCell(4).setCellValue(l.getDetalhes() != null ? l.getDetalhes() : "");
        }
        autoSizeColumns(sheet, 5);
    }

    // ==================== LEITURA ====================

    public void importarDoExcel() {
        synchronized (lock) {
            File file = new File(excelPath);
            if (!file.exists()) {
                log.warn("⚠️ Arquivo Excel não encontrado: {}. Exportando dados atuais...", excelPath);
                exportarTodosParaExcel();
                return;
            }

            try (FileInputStream fis = new FileInputStream(file);
                 Workbook wb = WorkbookFactory.create(fis)) {

                log.info("📂 Importando dados do Excel: {}", excelPath);
                // A importação efetiva sincroniza os dados externos de volta ao BD
                // Implementação simplificada: lê e loga as linhas
                Sheet sheet = wb.getSheet("Refeições");
                if (sheet != null) {
                    int rows = sheet.getLastRowNum();
                    log.info("📋 Encontradas {} linhas na aba Refeições", rows);
                }
            } catch (Exception e) {
                log.error("❌ Erro ao importar Excel: {}", e.getMessage());
            }
        }
    }

    // ==================== HELPERS ====================

    private Sheet getOrCreateSheet(Workbook wb, String name) {
        Sheet sheet = wb.getSheet(name);
        if (sheet == null) {
            sheet = wb.createSheet(name);
        } else {
            // Limpa as linhas existentes (exceto o cabeçalho)
            for (int i = sheet.getLastRowNum(); i >= 1; i--) {
                Row row = sheet.getRow(i);
                if (row != null) sheet.removeRow(row);
            }
        }
        return sheet;
    }

    private void criarCabecalho(Row row, String[] colunas) {
        for (int i = 0; i < colunas.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(colunas[i]);
        }
    }

    private void autoSizeColumns(Sheet sheet, int numCols) {
        for (int i = 0; i < numCols; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private String fmt(LocalDateTime dt) {
        return dt != null ? dt.format(FMT) : "";
    }
}

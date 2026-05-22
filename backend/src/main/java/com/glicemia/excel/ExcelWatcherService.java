package com.glicemia.excel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExcelWatcherService {

    private final ExcelService excelService;

    @Value("${app.excel.path}")
    private String excelPath;

    private long lastModified = 0L;

    @EventListener(ApplicationReadyEvent.class)
    public void onStart() {
        File file = new File(excelPath);
        if (file.exists()) {
            lastModified = file.lastModified();
            log.info("📂 Monitorando arquivo Excel: {}", excelPath);
            excelService.importarDoExcel();
        } else {
            log.info("📊 Arquivo Excel não encontrado. Será criado na primeira operação.");
            excelService.exportarTodosParaExcel();
            lastModified = new File(excelPath).lastModified();
        }
    }

    /**
     * Polling a cada 5 minutos para detectar alterações externas no Excel.
     */
    @Scheduled(fixedDelay = 300000)
    public void checkExcelModified() {
        File file = new File(excelPath);
        if (!file.exists()) return;

        long currentModified = file.lastModified();
        if (currentModified > lastModified) {
            log.info("🔄 Alteração detectada no Excel! Sincronizando com o banco de dados...");
            lastModified = currentModified;
            // Aguarda 200ms para o Excel terminar de escrever
            try { Thread.sleep(200); } catch (InterruptedException ignored) {}
            excelService.importarDoExcel();
        }
    }

    /**
     * Monitoramento via WatchService em thread separada (alternativo ao polling).
     */
    public void iniciarWatchService() {
        File file = new File(excelPath);
        Path dir = file.toPath().getParent();
        if (dir == null) dir = Path.of(".");

        final Path watchDir = dir;
        final String fileName = file.getName();

        Thread watchThread = new Thread(() -> {
            try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
                watchDir.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
                log.info("👁️ WatchService iniciado para: {}", watchDir);

                while (!Thread.currentThread().isInterrupted()) {
                    WatchKey key = watcher.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        if (event.kind() == StandardWatchEventKinds.OVERFLOW) continue;
                        Path changed = (Path) event.context();
                        if (changed.toString().equals(fileName)) {
                            log.info("🔄 WatchService detectou modificação em: {}", fileName);
                            Thread.sleep(200);
                            excelService.importarDoExcel();
                        }
                    }
                    key.reset();
                }
            } catch (Exception e) {
                log.warn("⚠️ WatchService encerrado: {}", e.getMessage());
            }
        }, "excel-watcher");
        watchThread.setDaemon(true);
        watchThread.start();
    }
}

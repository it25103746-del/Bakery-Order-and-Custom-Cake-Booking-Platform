package com.bakery.controller;

import com.bakery.model.BakeryRecord;
import com.bakery.service.FileRecordRepository;
import com.bakery.service.ModuleCatalog;
import com.bakery.service.ModuleDefinition;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.util.Map;

@Controller
public class ModuleController {
    private final ModuleCatalog moduleCatalog;
    private final FileRecordRepository repository;

    public ModuleController(ModuleCatalog moduleCatalog, FileRecordRepository repository) {
        this.moduleCatalog = moduleCatalog;
        this.repository = repository;
    }

    @GetMapping("/modules/{moduleKey}")
    public String list(@PathVariable String moduleKey, Model model) {
        ModuleDefinition<?> module = moduleCatalog.findByKey(moduleKey);
        model.addAttribute("module", module);
        model.addAttribute("modules", moduleCatalog.findAll());
        model.addAttribute("records", repository.findAll(module.getFileName(), module.getFactory()));
        return "module";
    }

    @GetMapping("/modules/{moduleKey}/new")
    public String createForm(@PathVariable String moduleKey, Model model) {
        ModuleDefinition<?> module = moduleCatalog.findByKey(moduleKey);
        model.addAttribute("module", module);
        model.addAttribute("record", module.getFactory().get());
        model.addAttribute("mode", "create");
        return "form";
    }

    @GetMapping("/modules/{moduleKey}/{id}/edit")
    public String editForm(@PathVariable String moduleKey, @PathVariable String id, Model model) {
        ModuleDefinition<?> module = moduleCatalog.findByKey(moduleKey);
        BakeryRecord record = repository.findById(module.getFileName(), module.getFactory(), id)
                .orElseThrow(() -> new IllegalArgumentException("Record not found: " + id));
        model.addAttribute("module", module);
        model.addAttribute("record", record);
        model.addAttribute("mode", "edit");
        return "form";
    }

    @PostMapping("/modules/{moduleKey}")
    public String save(@PathVariable String moduleKey, @RequestParam Map<String, String> params) {
        ModuleDefinition<?> module = moduleCatalog.findByKey(moduleKey);
        BakeryRecord record = module.getFactory().get();
        record.setId(params.getOrDefault("id", "").isBlank() ? createId(moduleKey) : params.get("id"));
        applyFields(module, record, params);
        repository.save(module.getFileName(), module.getFactory(), record);
        return "redirect:/modules/" + moduleKey;
    }

    @PostMapping("/modules/{moduleKey}/{id}/delete")
    public String delete(@PathVariable String moduleKey, @PathVariable String id) {
        ModuleDefinition<?> module = moduleCatalog.findByKey(moduleKey);
        repository.deleteById(module.getFileName(), module.getFactory(), id);
        return "redirect:/modules/" + moduleKey;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void applyFields(ModuleDefinition module, BakeryRecord record, Map<String, String> params) {
        for (Object fieldObject : module.getFields()) {
            ModuleDefinition.FieldDefinition field = (ModuleDefinition.FieldDefinition) fieldObject;
            field.assign(record, params.getOrDefault(field.name(), ""));
        }
    }

    private String createId(String moduleKey) {
        return moduleKey.toUpperCase().replace("-", "") + "-" + Instant.now().toEpochMilli();
    }
}

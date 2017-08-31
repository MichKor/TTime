package pl.com.tt.ttime.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.com.tt.ttime.model.Template;
import pl.com.tt.ttime.model.User;
import pl.com.tt.ttime.repository.TemplateRepository;
import pl.com.tt.ttime.service.TemplateService;

import java.util.List;

@Service
public class TemplateServiceImpl extends AbstractServiceImpl<Template, Long, TemplateRepository> implements TemplateService {

    @Autowired
    public TemplateServiceImpl(TemplateRepository repository) {
        super(repository);
    }

    @Override
    public Template save(Template template) {
        return super.save(template);
    }

    @Override
    public void delete(Template template) {
        detachFromUser(template);
        super.delete(template);
    }

    @Override
    public void deleteById(Long aLong) {
        Template template = repository.findOne(aLong);
        detachFromUser(template);
        super.deleteById(aLong);
    }

    @Override
    public void deleteAll() {
        List<Template> templates = repository.findAll();
        for (Template t : templates) {
            detachFromUser(t);
        }
        super.deleteAll();
    }

    private void detachFromUser(Template template) {
        User user = template.getUser();
        if (user.getDefaultTemplate() != null && user.getDefaultTemplate().getId().equals(template.getId())) {
            user.setDefaultTemplate(null);
        }
    }
}

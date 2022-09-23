package ch.martinelli.demo.views.editablegrid;

import ch.martinelli.demo.data.entity.SamplePerson;
import ch.martinelli.demo.data.service.SamplePersonService;
import ch.martinelli.demo.views.MainLayout;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Shortcuts;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@PageTitle("Editable Grid")
@Route(value = "editable-grid", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@Uses(Icon.class)
public class EditableGridView extends Div {

    private final Editor<SamplePerson> editor;
    private final Grid<SamplePerson> grid = new Grid<>(SamplePerson.class, false);

    private final BeanValidationBinder<SamplePerson> binder = new BeanValidationBinder<>(SamplePerson.class);

    private final SamplePersonService samplePersonService;

    @Autowired
    public EditableGridView(SamplePersonService samplePersonService) {
        this.samplePersonService = samplePersonService;

        addClassNames("master-detail-view");

        // Create Grid Editor
        editor = grid.getEditor();
        editor.setBinder(binder);

        // Configure Grid
        var colFirstName = grid.addColumn("firstName");
        var txtFirstName = new TextField();
        binder.forField(txtFirstName).bind("firstName");
        colFirstName.setEditorComponent(txtFirstName);

        var colLastName = grid.addColumn("lastName");
        var txtLastName = new TextField();
        binder.forField(txtLastName).bind("lastName");
        colLastName.setEditorComponent(txtLastName);

        var colEmail = grid.addColumn("email");
        var txtEmail = new TextField();
        binder.forField(txtEmail).bind("email");
        colEmail.setEditorComponent(txtEmail);

        var colPhone = grid.addColumn("phone");
        var txtPhone = new TextField();
        binder.forField(txtPhone).bind("phone");
        colPhone.setEditorComponent(txtPhone);

        var colDateOfBirth = grid.addColumn("dateOfBirth");
        var dpDateOfBirth = new DatePicker();
        binder.forField(dpDateOfBirth).bind("dateOfBirth");
        colDateOfBirth.setEditorComponent(dpDateOfBirth);

        var colOccupation = grid.addColumn("occupation");
        var txtOccupation = new TextField();
        binder.forField(txtOccupation).bind("occupation");
        colOccupation.setEditorComponent(txtOccupation);

        var importantRenderer = LitRenderer.<SamplePerson>of(
                        "<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>")
                .withProperty("icon", important -> important.isImportant() ? "check" : "minus").withProperty("color",
                        important -> important.isImportant()
                                ? "var(--lumo-primary-text-color)"
                                : "var(--lumo-disabled-text-color)");
        var colImportant = grid.addColumn(importantRenderer).setHeader("Important");
        var cbImportant = new Checkbox();
        binder.forField(cbImportant).bind("important");
        colImportant.setEditorComponent(cbImportant);

        grid.setItems(query -> samplePersonService.list(
                        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            closeEditorAndSave();
            grid.getEditor().editItem(event.getValue());
        });

        Shortcuts.addShortcutListener(this, this::closeEditorAndSave, Key.ENTER)
                .listenOn(txtFirstName, txtLastName, txtEmail, txtPhone, dpDateOfBirth, txtOccupation, cbImportant);

        add(grid);
    }

    private void closeEditorAndSave() {
        if (editor.isOpen()) {
            samplePersonService.update(binder.getBean());
            editor.cancel();
        }
    }

}

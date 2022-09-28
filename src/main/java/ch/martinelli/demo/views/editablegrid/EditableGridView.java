package ch.martinelli.demo.views.editablegrid;

import ch.martinelli.demo.data.entity.SamplePerson;
import ch.martinelli.demo.data.service.SamplePersonService;
import ch.martinelli.demo.views.MainLayout;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Shortcuts;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
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

import java.util.Optional;

@PageTitle("Editable Grid")
@Route(value = "editable-grid", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@Uses(Icon.class)
public class EditableGridView extends Div {

    private Optional<Grid.Column<SamplePerson>> currentColumn = Optional.empty();
    private Optional<SamplePerson> currentItem = Optional.empty();

    @Autowired
    public EditableGridView(SamplePersonService samplePersonService) {
        addClassNames("master-detail-view");

        var grid = new Grid<>(SamplePerson.class, false);
        var binder = new BeanValidationBinder<>(SamplePerson.class);

        // Create Grid Editor
        var editor = grid.getEditor();
        editor.setBinder(binder);
        editor.setBuffered(true);

        // Save Listener to save the changed SamplePerson
        editor.addSaveListener(event -> {
            SamplePerson item = event.getItem();
            samplePersonService.update(item);
        });

        // Configure Grid
        var txtFirstName = new TextField();
        txtFirstName.setWidthFull();
        binder.forField(txtFirstName).bind("firstName");
        grid.addColumn("firstName").setEditorComponent(txtFirstName).setAutoWidth(true);

        var txtLastName = new TextField();
        txtLastName.setWidthFull();
        binder.forField(txtLastName).bind("lastName");
        grid.addColumn("lastName").setEditorComponent(txtLastName).setAutoWidth(true);

        var txtEmail = new TextField();
        txtEmail.setWidthFull();
        binder.forField(txtEmail).bind("email");
        grid.addColumn("email").setEditorComponent(txtEmail).setAutoWidth(true);

        var txtPhone = new TextField();
        txtPhone.setWidthFull();
        binder.forField(txtPhone).bind("phone");
        grid.addColumn("phone").setEditorComponent(txtPhone).setAutoWidth(true);

        var dpDateOfBirth = new DatePicker();
        dpDateOfBirth.setWidthFull();
        binder.forField(dpDateOfBirth).bind("dateOfBirth");
        grid.addColumn("dateOfBirth").setEditorComponent(dpDateOfBirth).setAutoWidth(true);

        var txtOccupation = new TextField();
        txtOccupation.setWidthFull();
        binder.forField(txtOccupation).bind("occupation");
        grid.addColumn("occupation").setEditorComponent(txtOccupation).setAutoWidth(true);

        var cbImportant = new Checkbox();
        binder.forField(cbImportant).bind("important");
        var importantRenderer = LitRenderer.<SamplePerson>of(
                        "<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>")
                .withProperty("icon", important -> important.isImportant() ? "check" : "minus").withProperty("color",
                        important -> important.isImportant()
                                ? "var(--lumo-primary-text-color)"
                                : "var(--lumo-disabled-text-color)");
        grid.addColumn(importantRenderer).setHeader("Important").setEditorComponent(cbImportant);

        grid.setItems(query -> samplePersonService.list(
                        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // If a row is selected open the SamplePerson in the editor
        grid.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(samplePerson -> {
            editor.save();

            if (!editor.isOpen()) {
                grid.getEditor().editItem(samplePerson);

                currentColumn.ifPresent(column -> {
                    if (column.getEditorComponent() instanceof Focusable<?> focusable) {
                        focusable.focus();
                    }
                });
            }
        }));

        grid.addCellFocusListener(event -> {
            // Store the item on cell focus. Used in the ENTER ShortcutListener
            currentItem = event.getItem();
            // Store the current column. Used in the SelectionListener to focus the editor component
            currentColumn = event.getColumn();
        });

        // Select row on enter
        Shortcuts.addShortcutListener(grid, event -> currentItem.ifPresent(grid::select), Key.ENTER).listenOn(grid);

        // Cancel the editor on Escape
        Shortcuts.addShortcutListener(grid, () -> {
            if (editor.isOpen()) {
                editor.cancel();
            }
        }, Key.ESCAPE).listenOn(grid);

        add(grid);
    }

}

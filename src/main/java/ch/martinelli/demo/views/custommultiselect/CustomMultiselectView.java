package ch.martinelli.demo.views.custommultiselect;

import ch.martinelli.demo.data.entity.SamplePerson;
import ch.martinelli.demo.data.service.SamplePersonService;
import ch.martinelli.demo.views.MainLayout;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

@PageTitle("Custom Multiselect")
@Route(value = "custom-multiselect", layout = MainLayout.class)
@Uses(Icon.class)
public class CustomMultiselectView extends Div {

    private final Set<Checkbox> checkboxes = new HashSet<>();
    private Grid<SamplePerson> grid = new Grid<>(SamplePerson.class, false);
    private Set<SamplePerson> selectedPersons = new HashSet<>();

    @Autowired
    public CustomMultiselectView(SamplePersonService samplePersonService) {
        addClassNames("master-detail-view");

        // Configure Grid
        Checkbox selectAll = new Checkbox();
        selectAll.addValueChangeListener(event -> {
            if (event.getValue()) {
                selectedPersons.addAll(grid.getListDataView().getItems().toList());
            } else {
                selectedPersons.clear();
            }
            checkboxes.forEach(checkbox -> checkbox.setValue(event.getValue()));
        });

        grid.addComponentColumn(samplePerson -> {
            Checkbox checkbox = new Checkbox();
            checkboxes.add(checkbox);
            checkbox.addValueChangeListener(event -> {
                // Make sure that equals and hashCode is implemented!
                if (event.getValue()) {
                    selectedPersons.add(samplePerson);
                } else {
                    selectedPersons.remove(samplePerson);
                }
            });
            return checkbox;
        }).setHeader(selectAll);
        grid.addColumn("firstName");
        grid.addColumn("lastName");
        grid.addColumn("email");
        grid.addColumn("phone");
        grid.addColumn("dateOfBirth");
        grid.addColumn("occupation");
        LitRenderer<SamplePerson> importantRenderer = LitRenderer.<SamplePerson>of(
                        "<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>")
                .withProperty("icon", important -> important.isImportant() ? "check" : "minus").withProperty("color",
                        important -> important.isImportant()
                                ? "var(--lumo-primary-text-color)"
                                : "var(--lumo-disabled-text-color)");

        grid.addColumn(importantRenderer).setHeader("Important");

        grid.setItems(samplePersonService.list());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        grid.addSelectionListener(selectionEvent -> {
            // Do something with the selected items
            Set<SamplePerson> items = selectionEvent.getAllSelectedItems();
        });

        add(grid);
    }
}

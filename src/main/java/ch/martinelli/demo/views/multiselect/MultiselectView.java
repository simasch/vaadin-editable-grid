package ch.martinelli.demo.views.multiselect;

import ch.martinelli.demo.data.entity.SamplePerson;
import ch.martinelli.demo.data.service.SamplePersonService;
import ch.martinelli.demo.views.MainLayout;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.Set;

@PageTitle("Multiselect")
@Route(value = "multiselect", layout = MainLayout.class)
@Uses(Icon.class)
public class MultiselectView extends Div {


    private Grid<SamplePerson> grid = new Grid<>(SamplePerson.class, false);

    @Autowired
    public MultiselectView(SamplePersonService samplePersonService) {
        addClassNames("master-detail-view");

        // Configure Grid
        grid.setSelectionMode(Grid.SelectionMode.MULTI);

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

        grid.setItems(query -> samplePersonService.list(
                        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        grid.addSelectionListener(selectionEvent -> {
            // Do something with the selected items
            Set<SamplePerson> items = selectionEvent.getAllSelectedItems();
        });

        add(grid);
    }
}

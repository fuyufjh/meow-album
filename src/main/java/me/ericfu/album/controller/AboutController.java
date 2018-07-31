package me.ericfu.album.controller;

import me.ericfu.album.exception.ResourceNotFoundException;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

@Controller
public class AboutController {

    private static final String CHANGELOG_FILE = "CHANGELOG.md";

    private final Parser markdownParser = Parser.builder().build();
    private final HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();


    @GetMapping("/about")
    public ModelAndView about() {
        URL url = getClass().getClassLoader().getResource(CHANGELOG_FILE);
        if (url == null) {
            throw new ResourceNotFoundException(CHANGELOG_FILE + " not found");
        }

        String html;
        try (FileReader reader = new FileReader(new File(url.getFile()))) {
            Node node = markdownParser.parseReader(reader);
            html = htmlRenderer.render(node);
        } catch (FileNotFoundException e) {
            throw new ResourceNotFoundException(CHANGELOG_FILE + " not found");
        } catch (IOException e) {
            throw new RuntimeException("Failed to read changelog file: " + e.getMessage(), e.getCause());
        }

        ModelAndView view = new ModelAndView("about");
        view.addObject("changelogHtml", html);
        return view;
    }
}

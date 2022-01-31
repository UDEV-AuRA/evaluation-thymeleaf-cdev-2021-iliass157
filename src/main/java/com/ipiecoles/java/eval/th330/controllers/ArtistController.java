package com.ipiecoles.java.eval.th330.controllers;

import com.ipiecoles.java.eval.th330.model.Album;
import com.ipiecoles.java.eval.th330.model.Artist;
import com.ipiecoles.java.eval.th330.service.AlbumService;
import com.ipiecoles.java.eval.th330.service.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.persistence.EntityNotFoundException;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping(value = "/artists")
public class ArtistController {

    @Autowired
    ArtistService artistService;

    @Autowired
    AlbumService albumService;

    @RequestMapping(
            method = RequestMethod.GET, //Méthode HTTP : GET/POST/PATCH/PUT/DELETE
            //consumes = "", //Type MIME des données passées avec la requête : JSON, XML, Texte...
            //produces = "application/json", //Type MIME des données fournies dans la réponse
            value = "/{id}" //si on rajoute un /hello il sajoute à la suite du RequestMapping en haut de la classe dans le lien
    )
    public String getArtistById(final ModelMap model, @PathVariable("id") Long id) {
        Artist artist = artistService.findById(id);
        model.put("artist", artist);
        model.put("creation", false);
        return "detailArtist";
    }

    @RequestMapping(
            method = RequestMethod.GET,
            params = "name",
            value = "")
    public String searchArtistByNom( final ModelMap model,
                                     @RequestParam(required = true) String name,
                                     @RequestParam(defaultValue = "0") Integer page,
                                     @RequestParam(defaultValue = "10") Integer size,
                                     @RequestParam(defaultValue = "ASC") String sortDirection,
                                     @RequestParam(defaultValue = "name") String sortProperty
                                    ){
        Page<Artist> resRecherche = artistService.findByNameLikeIgnoreCase(name, page, size, sortProperty, sortDirection);
        model.put("artists", resRecherche);
        model.put("pageNumber", page + 1);
        model.put("previousPage", page - 1);
        model.put("nextPage", page + 1);
        model.put("start", page * size + 1);
        model.put("end", (page) * size + resRecherche.getNumberOfElements());
        return "listeArtists";
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = ""
    )
    public String listEmployes(final ModelMap model,
                               @RequestParam(defaultValue = "0") Integer page,
                               @RequestParam(defaultValue = "10") Integer size,
                               @RequestParam(defaultValue = "ASC") String sortDirection,
                               @RequestParam(defaultValue = "name") String sortProperty){
        Page<Artist> allArtists = artistService.findAllArtists(page, size, sortProperty, sortDirection);
        model.put("artists", allArtists);
        model.put("pageNumber", page + 1);
        model.put("previousPage", page - 1);
        model.put("nextPage", page + 1);
        model.put("start", page * size + 1);
        model.put("end", (page) * size + allArtists.getNumberOfElements());
        return "listeArtists";
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/detail/new"
    )
    public String newEmploye(final ModelMap model){
        model.put("artist", new Artist());
        model.put("creation", true);
        return "detailArtist";
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/new",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public RedirectView saveArtist(Artist artist, RedirectAttributes attributes){
        return createOrUpdateArtist(artist, attributes);
    }

    private RedirectView createOrUpdateArtist(Artist artist, RedirectAttributes attributes){
        if(artist.getId() != null){
            artist = artistService.updateArtiste(artist.getId(), artist);
        } else {
            artist = artistService.creerArtiste(artist);
        }
        attributes.addFlashAttribute("type", "success");
        attributes.addFlashAttribute("message", "Enregistrement de l'artiste effectué !");
        return new RedirectView("/artists/" + artist.getId());
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/{id}/delete"
    )
    public RedirectView deleteArtist(@PathVariable Long id, RedirectAttributes attributes){
        try {
            artistService.deleteArtist(id);
            attributes.addFlashAttribute("type", "success");
            attributes.addFlashAttribute("message", "Suppression de l'artiste effectué !");
        }
        catch (Exception e){
            attributes.addFlashAttribute("type", "danger");
            attributes.addFlashAttribute("message", e.getMessage());
        }
        return new RedirectView("/artists?page=0&size=10&sortProperty=name&sortDirection=ASC");
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/{id}/albums/{idAlbum}/delete"
    )
    public RedirectView deleteAlbum(@PathVariable("id") Long id, @PathVariable("idAlbum") Long idAlbum, RedirectAttributes attributes){
       try {
            albumService.deleteAlbum(idAlbum);
            attributes.addFlashAttribute("type", "success");
            attributes.addFlashAttribute("message", "Suppression de l'album effectuée !");
        }
        catch (Exception e){
            attributes.addFlashAttribute("type", "danger");
            attributes.addFlashAttribute("message", e.getMessage());
        }
        return new RedirectView("/artists/" + id);
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/{id}/albums/add",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public RedirectView addAlbum(@PathVariable("id") Long id, Album album, RedirectAttributes attributes){
        try {
            album.setArtist(artistService.findById(id));
            albumService.creerAlbum(album);
            attributes.addFlashAttribute("type", "success");
            attributes.addFlashAttribute("message", "Ajout de l'album " + album.getTitle() + " effectuée !");
        }
        catch (Exception e){
            attributes.addFlashAttribute("type", "danger");
            attributes.addFlashAttribute("message", e.getMessage());
        }
        return new RedirectView("/artists/" + id);
    }

}

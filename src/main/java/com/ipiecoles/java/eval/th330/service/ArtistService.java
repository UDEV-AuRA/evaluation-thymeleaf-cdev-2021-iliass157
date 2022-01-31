package com.ipiecoles.java.eval.th330.service;

import com.ipiecoles.java.eval.th330.model.Album;
import com.ipiecoles.java.eval.th330.model.Artist;
import com.ipiecoles.java.eval.th330.repository.AlbumRepository;
import com.ipiecoles.java.eval.th330.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class ArtistService {

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private AlbumRepository albumRepository;

    public Page<Artist> findAllArtists(Integer page, Integer size, String sortProperty, String sortDirection) {
        if(page < 0 || size < 0){
            throw new IllegalArgumentException("Le numéro de page et la taille des pages ne peuvent pas être négatifs !");
        }
        Long nbPageMax = artistRepository.count() / size;
        if(page > nbPageMax){
            throw new IllegalArgumentException("Avec une taille de " + size + ", le numéro de page doit être compris entre 0 et " + nbPageMax);
        }
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(sortDirection),sortProperty);
            Pageable pageable = PageRequest.of(page,size,sort);
            return artistRepository.findAll(pageable);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Erreur lors de la recherche paginée ! Vérifier les paramètres !");
        }
    }

    public Artist findById(Long id) {
        Optional<Artist> artist = this.artistRepository.findById(id);
        if(!artist.isPresent()){
            throw new EntityNotFoundException("Impossible de trouver l'artiste d'identifiant " + id);
        }
        return artist.get();
    }

    public Long countAllArtists() {
        return artistRepository.count();
    }

    public Artist creerArtiste(Artist artist) {
        if(artistRepository.findByName(artist.getName()) != null){
            throw new EntityExistsException("Il existe déjà un artiste avec le nom " + artist.getName());
        }
        if(artist.getName().isEmpty()){
            throw new IllegalArgumentException("Renseignez un nom à l'artiste !");
        }
        return artistRepository.save(artist);
    }

    public void deleteArtist(Long id) {
        if(!artistRepository.existsById(id)){
            throw new EntityNotFoundException("Impossible de trouver l'artiste numero " + id);
        }
        artistRepository.deleteById(id);
    }

    public Artist updateArtiste(Long id, Artist artist) {
        if(!artistRepository.existsById(id)){
            throw new EntityNotFoundException("Impossible de trouver l'artiste " + artist.getName());
        }
        if(!artist.getId().equals(id)){
            throw new IllegalArgumentException("Incohérence entre l'identifiant de l'artiste et celui de l'URL");
        }
        if(artistRepository.findByName(artist.getName()) != null && !artist.getId().equals(id)){
            throw new EntityExistsException("Il existe déjà un employé avec le nom " + artist.getName());
        }
        return artistRepository.save(artist);
    }

    public Page<Artist> findByNameLikeIgnoreCase(String name, Integer page, Integer size, String sortProperty, String sortDirection) {
        if(page < 0 || size < 0){
            throw new IllegalArgumentException("Le numéro de page et la taille des pages ne peuvent pas être négatifs !");
        }
        Long nbPageMax = artistRepository.count() / size;
        if(page > nbPageMax){
            throw new IllegalArgumentException("Avec une taille de " + size + ", le numéro de page doit être compris entre 0 et " + nbPageMax);
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection),sortProperty);
        Pageable pageable = PageRequest.of(page,size,sort);
        Page<Artist> resultatRecherche = artistRepository.findByNameContainingIgnoreCase(name, pageable);

        if(resultatRecherche.isEmpty()){
            throw new EntityNotFoundException("Aucun résultat trouvé");
        }
        try {
            return resultatRecherche;
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Erreur lors de la recherche paginée ! Vérifier les paramètres !");
        }

    }

}

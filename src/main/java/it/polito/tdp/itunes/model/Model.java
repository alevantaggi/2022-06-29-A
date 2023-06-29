package it.polito.tdp.itunes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.itunes.db.ItunesDAO;

public class Model {
	
	private List<Album> allAlbum;
	private SimpleDirectedWeightedGraph<Album, DefaultWeightedEdge> graph;
	private ItunesDAO dao;
	private List<Album> bestPath;
	private int bestScore;
	
	public Model() {
		this.allAlbum= new ArrayList<>();
		this.graph= new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		this.dao= new ItunesDAO();
	}
	
	public void buildGraph(int n) {
		clearGraph();
		loadNodes(n);
		
		Graphs.addAllVertices(this.graph, this.allAlbum);
//		System.out.println(this.graph.vertexSet().size());
		
		for(Album a1: this.allAlbum) {
			for(Album a2: this.allAlbum) {
				int peso= a1.getNumSongs()-a2.getNumSongs();
				
				if (peso>0) 
					Graphs.addEdgeWithVertices(this.graph, a2, a1, peso);	
					
			}
		}
		System.out.println(this.graph.vertexSet().size());
		System.out.println(this.graph.edgeSet().size());

	}
	
	
	
	
	private void clearGraph() {
		this.graph= new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
	}

	private void loadNodes(int n) {
		if(this.allAlbum.isEmpty())
			this.allAlbum= this.dao.getFilteredAlbums(n);
	}

	public int getNumVertices() {
		return this.graph.vertexSet().size();
	}

	public int getNumEdges() {
		return this.graph.edgeSet().size();
	}
	
	private int getBilancio(Album a) {
		int bilancio=0;
		List<DefaultWeightedEdge> archiIn= new ArrayList<>(this.graph.incomingEdgesOf(a));
		List<DefaultWeightedEdge> archiOut= new ArrayList<>(this.graph.outgoingEdgesOf(a));
		
		for(DefaultWeightedEdge e: archiIn)
			bilancio+= this.graph.getEdgeWeight(e);
		
		for(DefaultWeightedEdge e: archiOut)
			bilancio-= this.graph.getEdgeWeight(e);
		
		return bilancio;		
	}
	
	public List<Album> getVertcices(){
		List<Album> allVertices= new ArrayList<>(this.graph.vertexSet());
		Collections.sort(allVertices);
		
		return allVertices;
	}
	
	public List<BilancioAlbum> getAdiacenti(Album a){
		List<BilancioAlbum> risultato= new ArrayList<>();
		List<Album> successori= new ArrayList<>(Graphs.successorListOf(this.graph, a));
		
		for(Album album: successori)
			risultato.add(new BilancioAlbum(album, getBilancio(album)));
		
		Collections.sort(risultato);
		
		return risultato;
	}
	
	public List<Album> getPath(Album source, Album target, int threshold){
		List<Album> parziale= new ArrayList<>();
		this.bestPath= new ArrayList<>();
		this.bestScore=0;
		parziale.add(source);
		
		ricorsione(parziale,target,threshold);
		
		return this.bestPath;

	}

	private void ricorsione(List<Album> parziale, Album target, int threshold) {
		Album current= parziale.get(parziale.size()-1);
		
		// Condizione di uscita 
		if(current.equals(target)) {
			
			// Controllo se questa soluzione è migliore del best
			if(getScore(parziale)> this.bestScore) {
				this.bestPath= new ArrayList<>(parziale);
				this.bestScore= getScore(parziale);
			}
			return;
		}
			
		// continuo ad aggiungere elementi in parziale 
		for(Album a: Graphs.successorListOf(this.graph, current)) {
			if( this.graph.getEdgeWeight(this.graph.getEdge(current,a) )>= threshold) {
				parziale.add(a);
				ricorsione(parziale, target, threshold);
				parziale.remove(a); //backtracking
			}
		}
		return;	
		
	}

	private int getScore(List<Album> parziale) {
		int count=0;
		Album source= parziale.get(0);
		
		for(Album a: parziale) {
			if(getBilancio(a)> getBilancio(source))
				count++;
		}
		
		return count;
	}
}

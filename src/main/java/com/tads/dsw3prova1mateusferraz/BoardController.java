package com.tads.dsw3prova1mateusferraz;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Singleton
@Path("/game")
public class BoardController {

    private final BoardService boardService;

    @Inject
    public BoardController() {
        System.out.println("CONSTRUCT");
        this.boardService = new BoardService();
    }

    @GET
    @Produces("application/json")
    public String startGame() {
        return boardService.startGame();
    }

    @POST
    @Consumes("text/plain")
    @Produces("application/json")
    public String moveBoard(String direction) {
        return boardService.execute(BoardMove.valueOf(direction));
    }

}

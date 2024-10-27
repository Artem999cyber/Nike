package Controller;

public class ClientController {

    private final ClientService ClientService;

    //@Autowired
    public ClientController(ClientService ClientService) {
        this.ClientService = ClientService;
    }
}
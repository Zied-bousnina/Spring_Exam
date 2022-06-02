package com.example.demo.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dao.ClientRepository;
import com.example.demo.dao.CompteRepository;
import com.example.demo.entities.Client;
import com.example.demo.entities.Compte;
import com.example.demo.entities.Operation;
import com.example.demo.metier.BanqueMetierImpl;
import com.example.demo.metier.IBanqueMetier;

@Controller
public class BanqueController {
	@Autowired
	private BanqueMetierImpl banqueMetier;
	
	@Autowired
	public CompteRepository compteRepository;
	
	@Autowired
	public ClientRepository clientRepository;
	
	@RequestMapping(value="/operations")
	public String index() {
		return "comptes";
	}
	
//	@RequestMapping(method = RequestMethod.PUT, value="/comptes/{id}")
//	public void updateCompte (@RequestBody Compte compte, @PathVariable String id)
//	{
//		compteS
//	}
//	
	@PutMapping("/comptes/{id}")
	public Compte updateCompte(@RequestBody Compte copt, @PathVariable("id") Long id) {
		return banqueMetier.updateCompte(id, copt);
	}
	
	///form update client
	@GetMapping("/clients/update")
	public String showUpdateFprm( Model model, Long code) {
		Client cll = clientRepository.findById(code).get();
		model.addAttribute("client", cll);
		return "frmupdateClient";
		
		
	}
	
	//Update client
	@PutMapping("/clients/updateClient/{id}")
	public Client updateClient( @RequestBody Client client, @PathVariable("id") Long id) throws Exception {
		
		return banqueMetier.updateClient(id,client);
	}
	
	@DeleteMapping("/comptes/{id}")
	public String deleteCompte(@PathVariable("id") Long CptId) {
		banqueMetier.deleteCById(CptId);
		return "delete :)";
	}
	
	
	@RequestMapping(value="/frmlAjouClient")
	public String frmAjoutCompte() {
		return "frmAjoutCompte";
	}
	@RequestMapping(value="/frmlAjouComptes")
	public String frmAjoutCcomtes() {
		return "frmAjoutComptess";
	}
	@RequestMapping(value="/comptes", method = RequestMethod.GET)
	public String afficher(Model model) {
		List<Compte> comptes = compteRepository.findAll();
		model.addAttribute("ListeCompte",comptes);
		return "fmlListComptes";
	}

	@PostMapping("/ajoutClient")
	public String createClient(Model model , @Validated @ModelAttribute("clients") Client client, BindingResult binding, RedirectAttributes flashMessages) throws Exception {
		if (binding.hasErrors()) {
			model.addAttribute("lcli", client);
			return "frmAjoutCompte";
		}
		Client optionalClient = clientRepository.save(client);
		
		return "redirect:/clients";
		
	}
	@PostMapping("/ajoutcompte")
	public String createCompte(Model model , @Validated @ModelAttribute("comptes") Compte compte, BindingResult binding, RedirectAttributes flashMessages) throws Exception {
		if (binding.hasErrors()) {
			model.addAttribute("lcli", compte);
			return "frmAjoutComptess";
		}
		Compte optionalcompte = compteRepository.save(compte);
		
		return "redirect:/comptes";
		
	}
	
	@RequestMapping(value="/clients", method = RequestMethod.GET)
	public String afficherC(Model model) {
		List<Client> clients = clientRepository.findAll();
		model.addAttribute("listClient",clients);
		return "client";
	}

	
	@RequestMapping(value="/consulterCompte", method = RequestMethod.GET)
	public String consulter(Model model,Long codeCompte,
			@RequestParam(name = "page",defaultValue = "0") int page ,
            @RequestParam(name = "size",defaultValue = "4") int size){

		try{
			model.addAttribute("codeCompte",codeCompte);
			
			Compte cp = banqueMetier.consulterCompte(codeCompte).get();
			
            Page<Operation> pageOperations = banqueMetier.listOperation(codeCompte,page,size);
            model.addAttribute("listOperations",pageOperations.getContent());
            int[] pages = new int[pageOperations.getTotalPages()];//nombre de pages
            model.addAttribute("pages",pages);
            model.addAttribute("compte",cp);
		}catch (Exception e){
			model.addAttribute("exception","Compte introuvable");
		}
		return "comptes";//meme vue comptes
	}
	
	 @RequestMapping(value="/saveOperation" ,method = RequestMethod.POST )
	    public String saveOperation(Model model ,  String typeOperation , Long codeCompte , double montant , Long codeCompte2){
	      try{
	          if(typeOperation.equals("VERS")){
	        	  banqueMetier.verser(codeCompte,montant);
	          }else if(typeOperation.equals("RETR")){
	        	  banqueMetier.retirer(codeCompte,montant);
	          }else if(typeOperation.equals("VIR")){
	        	  banqueMetier.virement(codeCompte,codeCompte2,montant);
	          }
	      }catch (Exception e){
	          model.addAttribute("error",e);
	          return "redirect:/consulterCompte?codeCompte="+codeCompte+"&error="+e.getMessage();
	      }

	        return "redirect:/consulterCompte?codeCompte="+codeCompte;
	    }
	
}

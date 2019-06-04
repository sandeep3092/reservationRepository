package com.example.demo;

import java.util.Arrays;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(ReservationRepository rr) {
		return args -> {
			Arrays.asList("Sandy,Meenu,SS".split(","))
					.forEach(n -> rr.save(new Reservation(n)));

			rr.findAll().forEach(System.out::println);
			rr.findByReservationName("Sandy").forEach(System.out::println);
		};
	}
}

@Controller
class ReservationMvcController {
	@RequestMapping("/reservations.php")
	String reservations(Model model) {
		model.addAttribute("reservations",this.reservationRepository.findAll());
		return "reservations";
	}
	
	@Autowired
	private ReservationRepository reservationRepository;
}

@Component//Automatically plugged into the profile
class ReservationRepositoryProcessor implements ResourceProcessor<Resource<Reservation>> {

	@Override
	public Resource<Reservation> process(Resource<Reservation> reservationResource) {
		reservationResource.add(new Link("http://s1.com/images/"+reservationResource.getContent().getId()+".jpg","profile-photo"));
		return reservationResource;
	}
}

@RestController	
class ReservationRestController {
	
	@RequestMapping("/reservations")
	Collection<Reservation> reservations(){
		return reservationRepository.findAll();
	}
	
	@Autowired
	private ReservationRepository reservationRepository;
}

@RepositoryRestResource
interface ReservationRepository extends JpaRepository<Reservation, Long> {

	// @Query("Select distinct reservationName from reservation") --can be done
	Collection<Reservation> findByReservationName(@Param("rn") String rn);
}

@Entity
class Reservation {

	@Id
	@GeneratedValue
	private Long id;

	private String reservationName;

	Reservation() {
	}

	public Reservation(String name) {
		this.reservationName = name;
	}

	public String getReservationName() {
		return reservationName;
	}

	public Long getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Reservations [id=" + id + ", reservationName=" + reservationName + "]";
	}
}

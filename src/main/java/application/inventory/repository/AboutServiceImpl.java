package application.inventory.repository;

import org.springframework.stereotype.Service;

import application.inventory.models.About;

@Service
public class AboutServiceImpl implements AboutService{

	@Override
	public About getInfo() {
		return new About("Inventory Service", "Storefront", "Stores all the inventory data");
	}

}

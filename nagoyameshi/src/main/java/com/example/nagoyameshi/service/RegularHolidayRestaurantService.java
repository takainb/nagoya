package com.example.nagoyameshi.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.RegularHoliday;
import com.example.nagoyameshi.entity.RegularHolidayRestaurant;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.repository.RegularHolidayRepository;
import com.example.nagoyameshi.repository.RegularHolidayRestaurantRepository;

@Service
public class RegularHolidayRestaurantService {

	private final RegularHolidayRestaurantRepository regularHolidayRestaurantRepository;
	private final RegularHolidayRepository regularHolidayRepository;

	public RegularHolidayRestaurantService(RegularHolidayRestaurantRepository regularHolidayRestaurantRepository,
			RegularHolidayRepository regularHolidayRepository) {
		this.regularHolidayRestaurantRepository = regularHolidayRestaurantRepository;
		this.regularHolidayRepository = regularHolidayRepository;
	}

	@Transactional
	public void create(List<Integer> regularHolidayIds, Restaurant restaurant) {

		for (Integer regularHolidayId : regularHolidayIds) {
			RegularHolidayRestaurant regularHolidayRestaurant = new RegularHolidayRestaurant();
			RegularHoliday regularHoliday = regularHolidayRepository.getReferenceById(regularHolidayId);

			regularHolidayRestaurant.setRestaurant(restaurant);
			regularHolidayRestaurant.setRegularHoliday(regularHoliday);

			regularHolidayRestaurantRepository.save(regularHolidayRestaurant);
		}

	}

	@Transactional
	public void update(List<Integer> regularHolidayIds, Restaurant restaurant) {

		List<RegularHolidayRestaurant> existingRegularHolidayRestaurants = regularHolidayRestaurantRepository
				.findByRestaurantOrderByRegularHoliday_IdAsc(restaurant);
		List<Integer> existingRegularHolidayIds = regularHolidayRestaurantRepository
				.findRegularHolidayIdsByRestaurantOrderByRegularHoliday_IdAsc(restaurant);

		if (regularHolidayIds == null) {
			// regularHolidayIdsがnullの場合はすべてのエンティティを削除する
			for (RegularHolidayRestaurant existingRegularHolidayRestaurant : existingRegularHolidayRestaurants) {
				regularHolidayRestaurantRepository.delete(existingRegularHolidayRestaurant);
			}
		} else {
			// 既存のエンティティが新しいリストに存在しない場合は削除する
			for (RegularHolidayRestaurant existingRegularHolidayRestaurant : existingRegularHolidayRestaurants) {
				if (!regularHolidayIds.contains(existingRegularHolidayRestaurant.getRegularHoliday().getId())) {
					regularHolidayRestaurantRepository.delete(existingRegularHolidayRestaurant);
				}
			}

			// 新しいIDが既存のエンティティに存在しない場合は新たにエンティティを作成する
			for (Integer newRegularHolidayId : regularHolidayIds) {
				if (!existingRegularHolidayIds.contains(newRegularHolidayId)) {
					RegularHolidayRestaurant regularHolidayRestaurant = new RegularHolidayRestaurant();
					RegularHoliday regularHoliday = regularHolidayRepository.getReferenceById(newRegularHolidayId);

					regularHolidayRestaurant.setRestaurant(restaurant);
					regularHolidayRestaurant.setRegularHoliday(regularHoliday);

					regularHolidayRestaurantRepository.save(regularHolidayRestaurant);
				}
			}
		}

	}

	@Transactional
	public void deleteByRestaurant(Restaurant restaurant) {

		regularHolidayRestaurantRepository.deleteByRestaurant(restaurant);

	}

}

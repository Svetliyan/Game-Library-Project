package app.category.service;

import app.category.model.Category;
import app.category.repository.CategoryRepository;
import app.user.model.User;
import app.web.dto.CreateCategoryRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CategoryService {

    public final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void createCategory(CreateCategoryRequest createCategoryRequest, User user) {
        Category category = Category.builder()
                .name(createCategoryRequest.getName())
                .build();

        if (existsByName(createCategoryRequest.getName())) {
            throw new IllegalStateException("Category with this name already exists");
        }

        categoryRepository.save(category);
    }

    public Boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }

    public Optional<Category> existsById(int id){
        return categoryRepository.findById(id);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getById(Integer categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + categoryId));
    }
}

package mate.academy.bookstore.service.impl;

import java.util.List;
import mate.academy.bookstore.dto.category.CategoryDto;
import mate.academy.bookstore.dto.category.CreateCategoryRequestDto;
import mate.academy.bookstore.service.CategoryService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
class CategoryServiceImpl implements CategoryService {
    @Override
    public List<CategoryDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public CategoryDto getById(Long id) {
        return null;
    }

    @Override
    public CategoryDto save(CreateCategoryRequestDto categoryDto) {
        return null;
    }

    @Override
    public CategoryDto update(Long id, CreateCategoryRequestDto categoryDto) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }
}

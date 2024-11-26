module;

#include <algorithm>
#include <initializer_list>

export module Containers:DynamicArray;

namespace Containers
{
	export namespace NonGeneric
	{
		class DynamicArray final
		{
		public:
			using Iterator = int*;
			using ConstIterator = const int*;
			using Type = int;

			DynamicArray() noexcept // Гарантия отсутсвия исключений
				: m_data{ nullptr }
				, m_size{ 0 }
			{ }

			explicit DynamicArray(size_t size) // Базовая гарантия
				: m_data{ new int[size] {} }
				, m_size{ size }
			{
			}

			DynamicArray(const DynamicArray& other) // Базовая гарантия
				: m_data{ new int[other.m_size] }
				, m_size{ other.m_size }
			{
				std::copy(other.m_data, other.m_data + other.m_size, m_data);
			}

			DynamicArray(DynamicArray&& other) noexcept // Гарантия отсутсвия исключений
				: m_data{ other.m_data }
				, m_size{ other.m_size }
			{
				other.m_data = nullptr;
				other.m_size = 0;
			}

			DynamicArray(const std::initializer_list<int>& list) // Базовая гарантия
				: m_data{ new int[list.size()] }
				, m_size{ list.size() }
			{
				std::copy(list.begin(), list.end(), m_data);
			}

			~DynamicArray() // Гарантия отсутсвия исключений (деструктор является noexcept по умолчанию)
			{
				delete[] m_data;
			}
#if defined(BAD)
			DynamicArray& operator=(const DynamicArray& other) // Нет гарантий безопасности относительно исключений
			{
				if (this == &other)
					return *this;
				this->~DynamicArray();
				m_data = new int[other.m_size];
				m_size = other.m_size;
				std::copy(other.m_data, other.m_data + other.m_size, m_data);
				return *this;
			}
#else
#if __cplusplus >= 201103L
			DynamicArray& operator=(const DynamicArray& other) // Строгая гарантия
			{
				if (this != &other)
				{
					DynamicArray temp = other;
					std::swap(*this, temp);
				}
				return *this;
			}
#else
			DynamicArray& operator=(const DynamicArray& other) // Строгая гарантия
			{
				if (this == &other)
					return *this;
				auto data = new int[other.m_size];
				auto size = other.m_size;
				std::copy(other.m_data, other.m_data + other.m_size, data);
				//-------------------------------//
				this->~DynamicArray();
				std::swap(m_data, data);
				std::swap(m_size, size);
				return *this;
			}
#endif
#endif
			DynamicArray& operator=(DynamicArray&& other) noexcept // Гарантия отсутсвия исключений
			{
				std::swap(m_data, other.m_data);
				std::swap(m_size, other.m_size);
				return *this;
			}

			DynamicArray& operator=(const std::initializer_list<int>& items) // Строгая гарантия
			{
				DynamicArray temp = items;
				std::swap(*this, temp);
				return *this;
			}

			size_t GetSize() const noexcept { return m_size; }; // Гарантия отсутсвия исключений

			const int& operator[](int index) const noexcept { return m_data[index]; }; // Гарантия отсутсвия исключений
			int& operator[](int index) noexcept { return m_data[index]; }; // Гарантия отсутсвия исключений

			Iterator begin() noexcept { return Iterator{ m_data }; } // Гарантия отсутсвия исключений
			Iterator end() noexcept { return Iterator{ m_data + m_size }; } // Гарантия отсутсвия исключений

			ConstIterator begin() const noexcept { return ConstIterator{ m_data }; } // Гарантия отсутсвия исключений
			ConstIterator end() const noexcept { return ConstIterator{ m_data + m_size }; } // Гарантия отсутсвия исключений

		private:
			int* m_data;
			size_t m_size;
		};
	}

	export namespace GenericV1
	{
		template <typename T>
		class DynamicArray final
		{
		public:
			using Iterator = T*;
			using ConstIterator = const T*;

			DynamicArray() noexcept // Гарантия отсутсвия исключений
				: m_data{ nullptr }
				, m_size{ 0 }
			{ }

			explicit DynamicArray(size_t size) // Базовая гарантия
				: m_data{ new T[size] {} }
				, m_size{ size }
			{
			}

			DynamicArray(const DynamicArray<T>& other) // Базовая гарантия
				: m_data{ new T[other.m_size]{} }
				, m_size{ other.m_size }
			{
				try
				{
					std::copy(other.m_data, other.m_data + other.m_size, m_data);
				}
				catch (...)
				{
					delete[] m_data;
					throw;
				}
			}

			DynamicArray(DynamicArray<T>&& other) noexcept // Гарантия отсутсвия исключений
				: m_data{ other.m_data }
				, m_size{ other.m_size }
			{
				other.m_data = nullptr;
				other.m_size = 0;
			}

			DynamicArray(const std::initializer_list<T>& list) // Базовая гарантия
				: m_data{ new T[list.size()]{} }
				, m_size{ list.size() }
			{
				try
				{
					std::copy(list.begin(), list.end(), m_data);
				}
				catch (...)
				{
					delete[] m_data;
					throw;
				}
			}

			~DynamicArray() // Гарантия отсутсвия исключений
			{
				delete[] m_data;
			}

			DynamicArray<T>& operator=(const DynamicArray<T>& other) // Строгая гарантия
			{
				if (this != &other)
				{
					DynamicArray<T> temp = other;
					std::swap(*this, temp);
				}
				return *this;
			}

			DynamicArray<T>& operator=(DynamicArray<T>&& other) noexcept // Гарантия отсутсвия исключений
			{
				std::swap(m_data, other.m_data);
				std::swap(m_size, other.m_size);
				return *this;
			}

			DynamicArray& operator=(const std::initializer_list<T>& items) // Строгая гарантия
			{
				DynamicArray<T> temp = items;
				std::swap(*this, temp);
				return *this;
			}

			size_t GetSize() const noexcept { return m_size; }; // Гарантия отсутсвия исключений

			const T& operator[](int index) const noexcept { return m_data[index]; }; // Гарантия отсутсвия исключений
			T& operator[](int index) noexcept { return m_data[index]; }; // Гарантия отсутсвия исключений

			Iterator begin() noexcept { return Iterator{ m_data }; } // Гарантия отсутсвия исключений
			Iterator end() noexcept { return Iterator{ m_data + m_size }; } // Гарантия отсутсвия исключений

			ConstIterator begin() const noexcept { return ConstIterator{ m_data }; } // Гарантия отсутсвия исключений
			ConstIterator end() const noexcept { return ConstIterator{ m_data + m_size }; } // Гарантия отсутсвия исключений

		private:
			T* m_data;
			size_t m_size;
		};
	}

	namespace
	{
		template<typename T>
		void construct(T* buf, const T& right)
		{
			new (buf) T{ right };
		}

		template<typename T>
		void destroy(T* object) noexcept
		{
			object->~T();
		}

		template<typename Iter>
		void destroy(Iter first, Iter last) noexcept
		{
			for (auto& it = first; it != last; --it)
				destroy(&*it);
		}

		template <typename T>
		class ArrayBuffer
		{
		public:
			ArrayBuffer(size_t size) // Базовая гарантия
				: m_data{ size > 0 ? (T*)::operator new(size * sizeof(T)) : nullptr}
				, m_size{ 0 }
			{ }

			ArrayBuffer(const ArrayBuffer&) = delete;
			ArrayBuffer& operator=(const ArrayBuffer&) = delete;

			ArrayBuffer(ArrayBuffer&& right) noexcept // Гарантия отсутсвия исключений
				: m_data{ right.m_data }
				, m_size{ right.m_size }
			{
				right.m_data = nullptr;
				right.m_size = 0;
			}

			ArrayBuffer& operator=(ArrayBuffer&& right) noexcept // Гарантия отсутсвия исключений
			{
				std::swap(m_data, right.m_data);
				std::swap(m_size, right.m_size);
				return *this;
			}

			~ArrayBuffer() // Гарантия отсутсвия исключений
			{
				destroy(m_data + m_size, m_data - 1);
				::operator delete(m_data);
			}

		protected:
			T* m_data;
			size_t m_size;
		};
	}
	
	export inline namespace GenericV2
	{
		template <typename T>
		class DynamicArray final : private ArrayBuffer<T>
		{
		private:
			using ArrayBuffer<T>::m_data;
			using ArrayBuffer<T>::m_size;

		public:
			using Iterator = T*;
			using ConstIterator = const T*;

			explicit DynamicArray(size_t size = 0) // Базовая гарантия
				: ArrayBuffer<T>{ size }
			{
				for (int i = 0; i < size; ++i)
				{
					construct(m_data + m_size, T{});
					m_size++;
				}
			}

			DynamicArray(const DynamicArray<T>& other) // Базовая гарантия
				: ArrayBuffer<T>{ other.m_size }
			{
				for (int i = 0; i < other.m_size; ++i)
				{
					construct(m_data + m_size, other[i]);
					m_size++;
				}
			}

			DynamicArray(DynamicArray<T>&& other) noexcept = default; // Гарантия отсутсвия исключений

			DynamicArray(const std::initializer_list<T>& list) // Базовая гарантия
				: ArrayBuffer<T>{ list.size()}
			{
				for (auto &item : list)
				{
					construct(m_data + m_size, item);
					m_size++;
				}
			}

			~DynamicArray() // Гарантия отсутсвия исключений
			{
			}

			DynamicArray<T>& operator=(const DynamicArray<T>& other) // Строгая гарантия
			{
				if (this != &other)
				{
					DynamicArray<T> temp = other;
					std::swap(*this, temp);
				}
				return *this;
			}

			DynamicArray<T>& operator=(DynamicArray<T>&& other) noexcept = default; // Гарантия отсутсвия исключений

			DynamicArray& operator=(const std::initializer_list<T>& items) // Строгая гарантия
			{
				DynamicArray<T> temp = items;
				std::swap(*this, temp);
				return *this;
			}

			size_t GetSize() const noexcept { return m_size; }; // Гарантия отсутсвия исключений

			const T& operator[](int index) const noexcept { return m_data[index]; }; // Гарантия отсутсвия исключений
			T& operator[](int index) noexcept { return m_data[index]; }; // Гарантия отсутсвия исключений

			Iterator begin() noexcept { return Iterator{ m_data }; } // Гарантия отсутсвия исключений
			Iterator end() noexcept { return Iterator{ m_data + m_size }; } // Гарантия отсутсвия исключений

			ConstIterator begin() const noexcept { return ConstIterator{ m_data }; } // Гарантия отсутсвия исключений
			ConstIterator end() const noexcept { return ConstIterator{ m_data + m_size }; } // Гарантия отсутсвия исключений
		};
	}
}
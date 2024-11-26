#if FORCE_RUN_ALL_TESTS == 1 || RUN_ADAPTERS_TESTS == 1
#include "gtest/gtest.h"
#include <memory>

import Containers;

namespace Containers::Tests
{
	template<typename T>
	void TestPeekFromEmptyStack(std::unique_ptr<IStack<T>> stack)
	{
		EXPECT_THROW(stack->Peek(), EmptyContainerException);
	}

	template<typename T>
	void TestPopFromEmptyStack(std::unique_ptr<IStack<T>> stack)
	{
		EXPECT_THROW(stack->Pop(), EmptyContainerException);
	}

	template<typename T>
	void TestPushPeekPop(std::unique_ptr<IStack<T>> stack, const std::initializer_list<T>& initList)
	{
		for (auto& element : initList)
			stack->Push(element);
		EXPECT_EQ(initList.size() == 0, stack->IsEmpty());
		for (auto it = rbegin(initList); it != rend(initList); ++it)
		{
			auto element = stack->Peek();
			EXPECT_EQ(*it, element);
			stack->Pop();
		}
		EXPECT_TRUE(stack->IsEmpty());
	}

	template<typename T>
	void TestPushInFullStack(const std::initializer_list<T>& initList, const T& lastElement)
	{
		auto stack = CreateStack<T>(initList.size());
		for (auto& element : initList)
			stack->Push(element);
		EXPECT_THROW(stack->Push(lastElement), FullContainerException);
		for (auto it = rbegin(initList); it != rend(initList); ++it)
		{
			auto element = stack->Peek();
			EXPECT_EQ(*it, element);
			stack->Pop();
		}
		EXPECT_TRUE(stack->IsEmpty());
	}

	TEST(StackBasedOnDynamicArrayTest, TestPeekFromEmptyStack)
	{
		TestPeekFromEmptyStack(CreateStack<int>(5));
	}

	TEST(StackBasedOnDynamicArrayTest, TestPopFromEmptyStack)
	{
		TestPopFromEmptyStack(CreateStack<int>(5));
	}

	TEST(StackBasedOnDynamicArrayTest, TestPushPeekPop)
	{
		TestPushPeekPop(CreateStack<int>(5), {1,2,3,4,5});
	}

	TEST(StackBasedOnDynamicArrayTest, TestPushInFullStack)
	{
		TestPushInFullStack({ 1,2,3,4,5 }, 6);
	}

	TEST(StackBasedOnForwardListTest, TestPeekFromEmptyStack)
	{
		TestPeekFromEmptyStack(CreateStack<int>());
	}

	TEST(StackBasedOnForwardListTest, TestPopFromEmptyStack)
	{
		TestPopFromEmptyStack(CreateStack<int>());
	}

	TEST(StackBasedOnForwardListTest, TestPushPeekPop)
	{
		TestPushPeekPop(CreateStack<int>(), { 1,2,3,4,5 });
	}
}
#endif
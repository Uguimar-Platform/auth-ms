import { createParamDecorator, ExecutionContext } from '@nestjs/common';

/**
 * Custom decorator to extract the current authenticated user from the request object.
 * 
 * This decorator allows you to inject the user directly into controller methods
 * by using `@User()` as a parameter decorator.
 * 
 * 
 * @param data - Additional data passed to the decorator (not used in this case).
 * @param ctx - The execution context, which provides access to the request object.
 * @returns The user object extracted from the request.
 * @throws Error if the user is not found in the request.
 */
export const User = createParamDecorator(
  (data: unknown, ctx: ExecutionContext) => {
    const request = ctx.switchToHttp().getRequest();

    if (!request.user) {
      throw new Error('User not found in request');
    }
    return request.user;
  },
);
